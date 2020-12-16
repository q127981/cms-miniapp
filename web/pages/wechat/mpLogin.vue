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
		if (e.data) {
			let currentUser = JSON.parse(e.data);
			uni.setStorageSync('isLogin', '1');
			uni.setStorageSync('accessToken', currentUser.token);
			uni.setStorageSync('currentUser', currentUser);
			uni.setStorageSync('platform', 'wechatMP');

			/*第一次注册*/
			if (currentUser.is_exist_user == 0) {
				uni.setStorageSync('source', 'login');
				uni.setStorageSync('isRegister', 1);
				uni.setStorageSync('check_register', 1);
				this.$app.bindMobile();
				return;
			}

			/*登录后跳转*/
			uni.setStorageSync('source', 'login');
			let originUrl = uni.getStorageSync('loginOriginUrl');
			if (originUrl) {
				let originUrlRoute = originUrl.split('?');
				uni.navigateTo({
					url: originUrl
				});
				// if (this.$app.tabBarUrl.includes(originUrlRoute[0])) {
				// 	uni.switchTab({
				// 		url: originUrl[0]
				// 	});
				// } else {
				// 	uni.navigateTo({
				// 		url: originUrl
				// 	});
				// }
			} else {
				// uni.switchTab({
				// 	url: '/pages/user/index'
				// });
				uni.switchTab({
					url: '/pages/user/index'
				});
			}
		}
	}
};
</script>

<style lang="scss" scoped></style>
