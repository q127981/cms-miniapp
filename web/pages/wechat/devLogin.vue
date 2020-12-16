<template>
	<view></view>
</template>

<script>
export default {
	onShow() {
		this.$initPageTitle(); //初始化页面标题
	},
	onLoad(e) {
		this.$loading('正在登录');
		this.$app.request({
			url: this.$api.wechat.devLogin,
			data: {
				user_id: 1
			},
			method: 'POST',
			dataType: 'json',
			success: res => {
				console.log(res);
				if (res.code == 0) {
					let currentUser = res.data;
					uni.setStorageSync('isLogin', '1');
					uni.setStorageSync('accessToken', currentUser.token);
					uni.setStorageSync('currentUser', currentUser);
					// #ifdef H5
					uni.navigateTo({
						url: '/pages/article/index'
					});
					// #endif
					
					// #ifndef H5
					uni.switchTab({
						url: '/pages/article/index'
					});
					// #endif
				} else {
					this.$alert(res.msg);
				}
			},
			complete: res => {
				uni.stopPullDownRefresh();
				uni.hideLoading();
			}
		});
	}
};
</script>

<style lang="scss" scoped></style>
