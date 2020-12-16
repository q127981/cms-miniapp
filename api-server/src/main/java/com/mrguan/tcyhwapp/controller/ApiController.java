package com.mrguan.tcyhwapp.controller;

import com.mrguan.tcyhwapp.entity.BasicException;
import com.mrguan.tcyhwapp.entity.PageInfo;
import com.mrguan.tcyhwapp.entity.SysCode;
import com.mrguan.tcyhwapp.entity.dto.IndexListDTO;
import com.mrguan.tcyhwapp.entity.vo.*;
import com.mrguan.tcyhwapp.service.ApiService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ApiController
 *
 * @author MrGuan
 * @date 2020/11/2
 */
@Api(tags = "api")
@RestController
@RequestMapping("/api/")
public class ApiController {

    @Autowired
    ApiService apiService;

    @PostMapping("getCategory")
    public List<CommonVO> getCategory() {
        return apiService.getCategory();
    }

    @PostMapping("getIndex")
    public IndexListVO getIndex(@RequestParam(required = false) Long category_id, @RequestParam Integer page_index, @RequestParam Integer page_size, @RequestParam(required = false) String keyword) {
        IndexListDTO dto = new IndexListDTO();
        dto.setCategory_id(category_id);
        dto.setPage_index(page_index);
        dto.setPage_size(page_size);
        dto.setKeyword(keyword);
        return apiService.getIndex(dto);
    }

    @PostMapping("detail")
    public DetailVO detail(@RequestParam Long id, @RequestParam Integer page_size) {
        return apiService.detail(id, page_size);
    }


    @PostMapping("miniAppLogin")
    public TokenVO miniAppLogin(@RequestParam String share_user_id, @RequestParam String code, @RequestParam String user_info
            , @RequestParam String encrypted_data, @RequestParam String iv, @RequestParam String signature) {
        return apiService.miniAppLogin(share_user_id, code, user_info, encrypted_data, iv, signature);
    }

    @PostMapping("login")
    public LoginVO login(@RequestParam String mobile, @RequestParam String password) {
        return apiService.login(mobile, password);
    }

    @PostMapping("register")
    public LoginVO register(@RequestParam String mobile, @RequestParam String nickname, @RequestParam String password, @RequestParam String verify_code) {
        return apiService.register(mobile, nickname, password, verify_code);
    }

    @PostMapping("comment")
    public PageInfo<CommentVO> comment(@RequestParam Long article_id, @RequestParam Integer page_index, @RequestParam Integer page_size) {
        return apiService.comment(article_id, page_index, page_size);
    }

    @PostMapping("commentDetail")
    public CommentVO commentDetail(@RequestParam Long id, @RequestParam Integer page_index, @RequestParam Integer page_size) {
        return apiService.commentDetail(id, page_index, page_size);
    }

    @PostMapping("commentLike")
    public Integer commentLike(@RequestParam Long comment_id) {
        return apiService.changeCollect(comment_id, 4);
    }

    @PostMapping("like")
    public Integer like(@RequestParam Long article_id) {
        return apiService.changeCollect(article_id, 2);
    }

    @PostMapping("favorite")
    public Integer favorite(@RequestParam Long article_id) {
        return apiService.changeCollect(article_id, 1);
    }

    @PostMapping("userInfo")
    public UserInfoVO userInfo() {
        return apiService.userInfo();
    }

    @PostMapping("favoriteList")
    public PageInfo<FavoriteVO> favoriteList(@RequestParam Integer page_index, @RequestParam Integer page_size) {
        return apiService.favoriteList(page_index, page_size);
    }

    @PostMapping("logout")
    public Integer logout() {
        return apiService.logout();
    }

    @PostMapping("updatePassword")
    public Integer updatePassword(@RequestParam String old_password, @RequestParam String new_password) {
        return apiService.updatePassword(old_password, new_password);
    }

    @PostMapping("noAuth")
    public void noAuth() {
        throw new BasicException(SysCode.TOKEN_NOT_NULL);
    }

    @PostMapping("userIndex")
    public UserInfoVO userIndex() {
        return apiService.userInfo();
    }

    @PostMapping("addComment")
    public PageInfo<CommentVO> addComment(@RequestParam Long article_id, @RequestParam String content, @RequestParam Integer page_size) {
        return apiService.addComment(article_id, content, page_size);
    }

    @PostMapping("addReply")
    public PageInfo<CommentVO> addReply(@RequestParam Long comment_id, @RequestParam Long pid, @RequestParam String content, @RequestParam Integer page_size) {
        return apiService.addReply(comment_id, pid, content, page_size);
    }
    @PostMapping("feedback")
    public Integer feedback(@RequestParam String content){
        return 0;
    }
}
