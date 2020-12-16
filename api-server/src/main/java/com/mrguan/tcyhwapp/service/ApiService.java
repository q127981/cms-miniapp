package com.mrguan.tcyhwapp.service;

import com.mrguan.tcyhwapp.entity.PageInfo;
import com.mrguan.tcyhwapp.entity.dto.IndexListDTO;
import com.mrguan.tcyhwapp.entity.vo.*;

import java.util.List;

public interface ApiService {
    List<CommonVO> getCategory();

    IndexListVO getIndex(IndexListDTO dto);

    DetailVO detail(Long id, Integer pageSize);

    TokenVO miniAppLogin(String share_user_id, String code, String user_info, String encrypted_data, String iv, String signature);

    PageInfo<CommentVO> comment(Long article_id, Integer page_index, Integer page_size);

    Integer changeCollect(Long comment_id, Integer i);

    CommentVO commentDetail(Long id, Integer page_index, Integer page_size);

    UserInfoVO userInfo();

    LoginVO login(String mobile, String password);

    LoginVO register(String mobile, String nickname, String password, String verify_code);

    PageInfo<FavoriteVO> favoriteList(Integer page_index, Integer page_size);

    Integer logout();

    Integer updatePassword(String old_password, String new_password);

    PageInfo<CommentVO> addComment(Long article_id, String content, Integer page_size);

    PageInfo<CommentVO> addReply(Long comment_id, Long pid, String content, Integer page_size);
}
