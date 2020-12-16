<template>
	<view class="page">
		<view class="status-bar"></view>
		<view class="content">
			<view class="logo" v-if="false"><image :lazy-load="true" src="../../static/images/logo.png"></image></view>
			<view class="list">
				<view class="item">
					<iconfont type="mobile"></iconfont>
					<input class="input" v-model="mobile" type="number" maxlength="11" placeholder="请输入手机号" />
				</view>
				<view class="item">
					<iconfont type="pwd"></iconfont>
					<input class="input" v-model="password" type="text" maxlength="32" placeholder="请输入密码" password="true" />
				</view>
			</view>
			<view class="button" hover-class="button-hover" @tap="bindLogin"><text>登录</text></view>
			<view class="other">
				<navigator url="forget" open-type="navigate">忘记密码</navigator>
				<text>|</text>
				<navigator url="register" open-type="navigate">注册账户</navigator>
			</view>
		</view>
	</view>
</template>

<script>
import { mapState, mapMutations } from 'vuex';
import iconfont from '@/components/iconfont/iconfont.vue';
import validator from '@/common/validator';
export default {
	components: {
		iconfont
	},
	data() {
		return {
			mobile: '',
			password: '',
			originUrl: '',
			hasLogin: false
		};
	},
	onUnload() {
		if (this.originUrl.search('/pages/common/login') != '-1') {
			uni.reLaunch({
				url: '../article/index'
			});
		}
	},
	onShow() {},
	onLoad(e) {
		let loginOriginUrl = uni.getStorageSync('loginOriginUrl');
		if (loginOriginUrl) {
			this.originUrl = loginOriginUrl;
			setTimeout(() => {
				uni.removeStorageSync('loginOriginUrl');
			}, 1000);
			console.log(this.originUrl);
		}
	},
	computed: mapState(['isLogin']),
	methods: {
		...mapMutations(['login']),
		bindLogin() {
			if (!validator.checkMobile(this.mobile)) {
				return;
			} else if (this.password == '') {
				this.$alert('请输入密码');
				return;
			}
			uni.showLoading({
				title: '登录中…'
			});
			this.$app.request({
				url: this.$api.user.login,
				data: {
					mobile: this.mobile,
					password: this.password
				},
				method: 'POST',
				dataType: 'json',
				success: res => {
					if (res.code == 0) {
						/*隐藏加载框*/
						uni.hideLoading();

						/*更新登录状态,保存用户数据*/
						this.hasLogin = true;
						this.login(res.data);
						uni.setStorageSync('platform', 'h5');
						uni.setStorageSync('source', 'login');

						/*登录后跳转*/
						console.log(this.originUrl);
						if (this.originUrl) {
							uni.navigateBack();
						} else {
							// #ifndef H5
							uni.switchTab({
								url: '/pages/article/index'
							});
							// #endif

							// #ifdef H5
							uni.navigateTo({
								url: '/pages/article/index'
							});
							// #endif
						}
					} else {
						this.$alert(res.msg);
					}
				}
			});
		}
	}
};
</script>

<style lang="scss">
.content {
	display: flex;
	flex-direction: column;
	justify-content: center;

	.logo {
		margin-top: 143rpx;
		margin-left: auto;
		margin-right: auto;
		image {
			height: 65rpx;
			width: 360rpx;
		}
	}

	.list {
		display: flex;
		flex-direction: column;
		padding-top: 120rpx;
		padding-left: 100rpx;
		padding-right: 100rpx;

		.item {
			display: flex;
			flex-direction: row;
			justify-content: space-between;
			align-items: center;
			height: 100rpx;
			border-bottom: 1upx solid rgba(230, 230, 230, 1);

			.icon {
				color: #ababab;
				margin-right: 5rpx;
				font-size: 40rpx;
			}

			.input {
				flex: 1;
				text-align: left;
				font-size: 30rpx;
				margin-left: 16rpx;
			}
		}
	}

	.button {
		margin-top: 100rpx;
	}

	.other {
		display: flex;
		flex-direction: row;
		justify-content: center;
		align-items: center;
		font-size: 30rpx;
		margin-top: 60rpx;
		color: #ffd100;
		text-align: center;
		height: 40rpx;
		line-height: 40rpx;

		text {
			font-size: 24rpx;
			margin-left: 15rpx;
			margin-right: 15rpx;
		}
	}
}
</style>
