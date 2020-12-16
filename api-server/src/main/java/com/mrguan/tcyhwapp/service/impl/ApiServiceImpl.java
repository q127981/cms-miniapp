package com.mrguan.tcyhwapp.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mrguan.tcyhwapp.config.WxMaConfiguration;
import com.mrguan.tcyhwapp.entity.BasicException;
import com.mrguan.tcyhwapp.entity.PageInfo;
import com.mrguan.tcyhwapp.entity.RedisKeyConstans;
import com.mrguan.tcyhwapp.entity.SysCode;
import com.mrguan.tcyhwapp.entity.dto.IndexListDTO;
import com.mrguan.tcyhwapp.entity.vo.*;
import com.mrguan.tcyhwapp.mapper.*;
import com.mrguan.tcyhwapp.model.CommentInfo;
import com.mrguan.tcyhwapp.model.ContentInfo;
import com.mrguan.tcyhwapp.model.UserCollectInfo;
import com.mrguan.tcyhwapp.model.UserInfo;
import com.mrguan.tcyhwapp.service.ApiService;
import com.mrguan.tcyhwapp.utils.JWTUtil;
import com.mrguan.tcyhwapp.utils.MD5Utils;
import com.mrguan.tcyhwapp.utils.SensitiveWordFilter;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ApiServiceImpl
 *
 * @author MrGuan
 * @date 2020/11/2
 */
@Service
@Slf4j
public class ApiServiceImpl implements ApiService {

    @Autowired
    CategoryInfoMapper categoryInfoMapper;

    @Autowired
    ContentInfoMapper contentInfoMapper;
    @Autowired
    CommentInfoMapper commentInfoMapper;
    @Autowired
    UserCollectInfoMapper userCollectInfoMapper;
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    RedisTemplate redisTemplate;

    @Value("${wx.miniapp.configs[0].appid}")
    private String appid;

    @Override
    public List<CommonVO> getCategory() {
        return categoryInfoMapper.getCategory();
    }

    @Override
    public IndexListVO getIndex(IndexListDTO dto) {
        if (null != dto.getKeyword()) {
            dto.setKeyword(dto.getKeyword().trim());
        }
        Page<IndexVO> page = PageHelper.startPage(dto.getPage_index(), dto.getPage_size())
                .doSelectPage(() -> contentInfoMapper.getIndex(dto.getCategory_id(), dto.getKeyword()));
        for (IndexVO x : page.getResult()) {
            x.setLink(new LinkVO(x.getId()));
        }
        List<SliderVO> slider = contentInfoMapper.getSlider(dto.getCategory_id(), dto.getKeyword());
        Integer pageCount = (int) page.getTotal() / dto.getPage_size() + ((int) page.getTotal() % dto.getPage_size() == 0 ? 0 : 1);
        IndexListVO vo = new IndexListVO(page.getTotal(), pageCount, page.getResult(), slider);
        return vo;
    }

    @Override
    public DetailVO detail(Long id, Integer page_size) {
        DetailVO result = contentInfoMapper.getById(id);
        if (!ObjectUtils.isEmpty(result)) {
            Page<CommentVO> page = null;
            try {
                JWTUtil.getUserId();
                page = PageHelper.startPage(1, 10).doSelectPage(() -> commentInfoMapper.getList(id, JWTUtil.getUserId()));
            } catch (Exception e) {
//            e.printStackTrace();
                page = PageHelper.startPage(1, 10).doSelectPage(() -> commentInfoMapper.getList(id, null));
            }
            Integer pageCount = (int) page.getTotal() / page_size + ((int) page.getTotal() % page_size == 0 ? 0 : 1);
            PageInfo commentPage = new PageInfo(page.getTotal(), pageCount, page.getResult());
            result.setComment(commentPage);
            contentInfoMapper.addRead(id);
        }
        return result;
    }

    @Override
    @Transactional
    public TokenVO miniAppLogin(String share_user_id, String code, String user_info, String encrypted_data, String iv, String signature) {
        final WxMaService wxService = WxMaConfiguration.getMaService(appid);
        try {
            WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo(code);
            WxMaUserInfo wxUserInfo = wxService.getUserService().getUserInfo(session.getSessionKey(),
                    encrypted_data, iv);
            UserInfo userInfo = userInfoMapper.getByOpenid(wxUserInfo.getOpenId());
            JSONObject json = JSONObject.parseObject(user_info);
            if (ObjectUtils.isEmpty(userInfo)) {
                userInfo = new UserInfo();
                userInfo.setOpen_id(wxUserInfo.getOpenId());
                userInfo.setAvatar_url(json.getString("avatarUrl"));
                userInfo.setNickname(json.getString("nickName"));
                userInfo.setGender(json.getInteger("gender"));
                userInfoMapper.insert(userInfo);
            } else {
                userInfo.setAvatar_url(json.getString("avatarUrl"));
                userInfo.setNickname(json.getString("nickName"));
                userInfo.setGender(json.getInteger("gender"));
                userInfoMapper.updateByPrimaryKey(userInfo);
            }
            TokenVO result = new TokenVO();
            result.setId(userInfo.getId());
            String token = JWTUtil.generateToken(userInfo.getId());
            redisTemplate.opsForValue().set(String.format(RedisKeyConstans.TOKEN, userInfo.getId()), token, 1, TimeUnit.DAYS);
            result.setToken(token);
            return result;
        } catch (WxErrorException e) {
            throw new BasicException(SysCode.GET_WX_INFO_ERROR);
        }
    }

    @Override
    public PageInfo<CommentVO> comment(Long article_id, Integer page_index, Integer page_size) {
        Page<CommentVO> page = null;
        try {
            JWTUtil.getUserId();
            page = PageHelper.startPage(page_index, page_size).doSelectPage(() -> commentInfoMapper.getList(article_id, JWTUtil.getUserId()));
        } catch (Exception e) {
//            e.printStackTrace();
            page = PageHelper.startPage(page_index, page_size).doSelectPage(() -> commentInfoMapper.getList(article_id, null));
        }
        Integer pageCount = (int) page.getTotal() / page_size + ((int) page.getTotal() % page_size == 0 ? 0 : 1);
        PageInfo commentPage = new PageInfo(page.getTotal(), pageCount, page.getResult());
        return commentPage;
    }

    /**
     * 1 收藏文章 2 点赞文章 3收藏评论 4点赞评论
     *
     * @param comment_id
     * @param type
     * @return
     */
    @Override
    @Transactional
    public Integer changeCollect(Long comment_id, Integer type) {
        Long userId = JWTUtil.getUserId();
        boolean flag = true;
        UserCollectInfo userCollectInfo = userCollectInfoMapper.getByIdAndType(comment_id, type, userId);
        if (ObjectUtils.isEmpty(userCollectInfo)) {
            flag = false;
            userCollectInfo = new UserCollectInfo();
            userCollectInfo.setC_id(comment_id);
            userCollectInfo.setType(type);
            userCollectInfo.setUser_id(userId);
            userCollectInfoMapper.insert(userCollectInfo);
        } else {
            if (userCollectInfo.getUser_id().equals(userId)) {
                userCollectInfoMapper.deleteByPrimaryKey(userCollectInfo.getId());
            }
        }
        ContentInfo contentInfo = null;
        CommentInfo commentInfo = null;
        switch (type) {
            case 2:
                contentInfoMapper.addLikeCount(flag ? -1 : 1, comment_id);
                break;
            case 4:
                commentInfoMapper.addLikeCount(flag ? -1 : 1, comment_id);
                break;
            default:
                break;
        }
        return 0;
    }

    @Override
    public CommentVO commentDetail(Long id, Integer page_index, Integer page_size) {
        CommentVO result = null;
        Page<CommentVO> page = null;
        try {
            Long userId = JWTUtil.getUserId();
            result = commentInfoMapper.getById(id, userId);
            page = PageHelper.startPage(page_index, page_size).doSelectPage(() -> commentInfoMapper.getDetailList(id, userId));
        } catch (Exception e) {
            result = commentInfoMapper.getById(id, null);
            page = PageHelper.startPage(page_index, page_size).doSelectPage(() -> commentInfoMapper.getDetailList(id, null));
        }
        Integer pageCount = (int) page.getTotal() / page_size + ((int) page.getTotal() % page_size == 0 ? 0 : 1);
        PageInfo commentPage = new PageInfo(page.getTotal(), pageCount, page.getResult());
        result.setReply(commentPage);
        return result;
    }

    @Override
    public UserInfoVO userInfo() {
        try {
            Long userId = JWTUtil.getUserId();
            UserInfoVO userInfo = userInfoMapper.getUserInfo(userId);
            if (!StringUtils.isEmpty(userInfo.getMobile())) {
                userInfo.setMobile(userInfo.getMobile().substring(0, 3) + "****" + userInfo.getMobile().substring(7, 11));
            }
            return userInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return new UserInfoVO();
        }
    }

    @Override
    public LoginVO login(String mobile, String password) {
        UserInfo userInfo = userInfoMapper.getByMobile(mobile);
        if (ObjectUtils.isEmpty(userInfo)) {
            throw new BasicException(SysCode.USER_NOT_EXISTS);
        }
        if (MD5Utils.getSaltverifyMD5(password, userInfo.getPassword())) {
            return getLoginVO(userInfo);
        } else {
            throw new BasicException(SysCode.PASSWORD_ERROR);
        }
    }

    @Override
    @Transactional
    public LoginVO register(String mobile, String nickname, String password, String verify_code) {
//        Boolean hasKey = redisTemplate.hasKey(String.format(RedisKeyConstans.VERIFY_CODE, mobile, verify_code));
        Boolean hasKey = true;
        if (hasKey) {
            UserInfo userInfo = userInfoMapper.getByMobile(mobile);
            if (!ObjectUtils.isEmpty(userInfo)) {
                throw new BasicException(SysCode.USER_ALREADY_EXISTS);
            }
            userInfo = new UserInfo();
            userInfo.setNickname(nickname);
            userInfo.setPassword(MD5Utils.getSaltMD5(password));
            userInfo.setPhone_number(mobile);
            userInfo.setAvatar_url("");
            userInfoMapper.insert(userInfo);
            return getLoginVO(userInfo);
        } else {
            throw new BasicException(SysCode.CODE_ERROR);
        }
    }

    @Override
    public PageInfo<FavoriteVO> favoriteList(Integer page_index, Integer page_size) {
        Long userId = JWTUtil.getUserId();
        Page<FavoriteVO> page = PageHelper.startPage(page_index, page_size).doSelectPage(() -> contentInfoMapper.getFavoriteList(userId));
        Integer pageCount = (int) page.getTotal() / page_size + ((int) page.getTotal() % page_size == 0 ? 0 : 1);
        PageInfo pageInfo = new PageInfo(page.getTotal(), pageCount, page.getResult());
        return pageInfo;
    }

    @Override
    public Integer logout() {
        try {
            Long userId = JWTUtil.getUserId();
            redisTemplate.delete(String.format(RedisKeyConstans.TOKEN, userId));
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    @Override
    @Transactional
    public Integer updatePassword(String old_password, String new_password) {
        Long userId = JWTUtil.getUserId();
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
        if (ObjectUtils.isEmpty(userInfo)) {
            throw new BasicException(SysCode.USER_NOT_EXISTS);
        }
        if (MD5Utils.getSaltverifyMD5(old_password, userInfo.getPassword())) {
            userInfo.setPassword(MD5Utils.getSaltMD5(new_password));
            return userInfoMapper.updateByPrimaryKey(userInfo);
        } else {
            throw new BasicException(SysCode.PASSWORD_ERROR);
        }
    }

    @Override
    @Transactional
    public PageInfo<CommentVO> addComment(Long article_id, String content, Integer page_size) {
        SensitiveWordFilter sw = new SensitiveWordFilter();
        sw.InitializationWork();
        String filted = sw.filterInfo(content);
        log.info("content:{}", content);
        log.info("filted:{}", filted);
        if (!content.equals(filted)) {
            throw new BasicException(SysCode.SENSITIVE_ERROR);
        }
        ContentInfo info = contentInfoMapper.selectByPrimaryKey(article_id);
        if (ObjectUtils.isEmpty(info)) {
            throw new BasicException(SysCode.NOT_EXISTS);
        }
        contentInfoMapper.addCommentCount(1, article_id);
        CommentInfo commentInfo = new CommentInfo();
        commentInfo.setC_id(article_id);
        commentInfo.setUser_id(JWTUtil.getUserId());
        commentInfo.setContent(content);
        commentInfo.setLike_count(0);
        commentInfo.setReply_count(0);
        commentInfoMapper.insert(commentInfo);
        return comment(article_id, 1, page_size);
    }

    @Override
    @Transactional
    public PageInfo<CommentVO> addReply(Long comment_id, Long pid, String content, Integer page_size) {
        SensitiveWordFilter sw = new SensitiveWordFilter();
        sw.InitializationWork();
        String filted = sw.filterInfo(content);
        log.info("content:{}", content);
        log.info("filted:{}", filted);
        if (!content.equals(filted)) {
            throw new BasicException(SysCode.SENSITIVE_ERROR);
        }
        CommentInfo info = commentInfoMapper.selectByPrimaryKey(comment_id);
        if (ObjectUtils.isEmpty(info)) {
            throw new BasicException(SysCode.NOT_EXISTS);
        }
        commentInfoMapper.addReplyCount(1, comment_id);
        CommentInfo commentInfo = new CommentInfo();
        commentInfo.setPid(comment_id);
        commentInfo.setUser_id(JWTUtil.getUserId());
        commentInfo.setContent(content);
        commentInfo.setLike_count(0);
        commentInfo.setReply_count(0);
        commentInfoMapper.insert(commentInfo);
        Page<CommentVO> page = PageHelper.startPage(1, page_size).doSelectPage(() -> commentInfoMapper.getDetailList(comment_id, JWTUtil.getUserId()));
        Integer pageCount = (int) page.getTotal() / page_size + ((int) page.getTotal() % page_size == 0 ? 0 : 1);
        PageInfo commentPage = new PageInfo(page.getTotal(), pageCount, page.getResult());
        return commentPage;
    }

    private LoginVO getLoginVO(UserInfo userInfo) {
        LoginVO result = new LoginVO();
        result.setAvatar_url(userInfo.getAvatar_url());
        result.setMobile(userInfo.getPhone_number());
        result.setNickname(userInfo.getNickname());
        result.setUser_id(userInfo.getId());
        String token = JWTUtil.generateToken(userInfo.getId());
        redisTemplate.opsForValue().set(String.format(RedisKeyConstans.TOKEN, userInfo.getId()), token, 1, TimeUnit.DAYS);
        result.setToken(token);
        return result;
    }


}
