<template>
	<view class="wrap">
		<view class="status-bar"></view>
		<view class="content">
			<view class="user">
				<view class="base-info">
					<view class="user-info">
						<view class="avatar" :class="[user.id > 0 && !user.avatar_url ? 'default-avatar' : '']">
							<navigator v-if="user.avatar_url" url="profile" hover-class="none">
								<image :src="user.avatar_url"></image>
							</navigator>
							<image v-else src="/static/images/default_user_photo.jpg" @tap="login()"></image>
						</view>
						<navigator v-if="user.id > 0" class="info" hover-class="none" url="/pages/user/profile">
							<view class="nickname">{{ user.nickname }}</view>
							<!-- <view class="mobile" v-if="user.mobile">{{ user.mobile }}</view> -->
						</navigator>
						<view v-else class="info">
							<view class="btn-login" @tap="login()">{{ loginText }}</view>
						</view>
						<view class="setting hidden" v-if="user.id > 0">
							<navigator class="btn-setting" url="/pages/user/profile">
								<iconfont type="modify-pen"></iconfont>
							</navigator>
						</view>
					</view>
				</view>
			</view>
			<view class="nav">
				<view class="list">
					<navigator class="item" url="/pages/user/favorite">
						<view class="info">
							<view class="name">
								<iconfont type="star-o"></iconfont>
								<text>我的收藏</text>
							</view>
							<iconfont type="go"></iconfont>
						</view>
					</navigator>
					<navigator class="item" url="/pages/user/profile">
						<view class="info">
							<view class="name">
								<iconfont type="user-info"></iconfont>
								<text>我的资料</text>
							</view>
							<iconfont type="go"></iconfont>
						</view>
					</navigator>
				</view>
				<view class="list">
					<navigator class="item" url="/pages/common/about">
						<view class="info">
							<view class="name">
								<iconfont type="about"></iconfont>
								<text>关于团节社成都邀请码</text>
							</view>
							<iconfont type="go"></iconfont>
						</view>
					</navigator>
					<navigator class="item" url="/pages/user/feedback">
						<view class="info">
							<view class="name">
								<iconfont type="feedback"></iconfont>
								<text>意见建议</text>
							</view>
							<iconfont type="go"></iconfont>
						</view>
					</navigator>
				</view>
			</view>
			<view class="tab-bar"></view>
		</view>
		<pageLoading v-if="showPageLoading"></pageLoading>
	</view>
</template>

<script>
	import iconfont from '@/components/iconfont/iconfont.vue';
	import pageLoading from '@/components/loading/pageLoading.vue';
	export default {
		components: {
			iconfont,
			pageLoading
		},
		data() {
			return {
				user: {},
				loginText: '登录',
				showPageLoading: true
			};
		},
		onShow() {
			// this.user = {};
			console.log('onShow2')
			this.$initPageTitle(); //初始化页面标题
			// #ifdef MP
			this.loginText = '微信登录';
			// #endif
			// let cancelLogin = uni.getStorageSync('cancelLogin');
			// console.log(cancelLogin)
			// if (cancelLogin) {
			// 	uni.removeStorageSync('cancelLogin');
			// }else{
			// 	this.getUserInfo(); //获取用户信息
			// }
			this.getUserInfo(); //获取用户信息

			// //登录
			// let source = uni.getStorageSync('source');
			// if (source == 'login' || source == 'storyEdit') {
			// 	uni.removeStorageSync('source');
			// }
		},
		onLoad(e) {},
		onPullDownRefresh() {
			uni.showLoading({
				title: '刷新中'
			});
			this.getUserInfo(); //获取用户信息
		},
		methods: {
			/*获取用户信息*/
			getUserInfo() {
				this.$app.request({
					url: this.$api.user.index,
					data: {},
					method: 'POST',
					dataType: 'json',
					success: res => {
						console.log(res);
						if (res.code == 0) {
							this.showPageLoading = false;
							this.user = res.data;
						} else {
							this.$alert(res.msg);
						}
					},
					complete: res => {
						uni.stopPullDownRefresh();
						uni.hideLoading();
					}
				});
			},

			/*登录*/
			login() {
				this.$app.login();
			},

			/*退出登录*/
			logout() {
				uni.showModal({
					title: '提示',
					content: '退出登录？',
					confirmText: '是',
					cancelText: '否',
					success: result => {
						if (result.confirm) {
							this.$app.request({
								url: this.$api.user.logout,
								method: 'POST',
								success: res => {
									if (res.code == 0) {
										uni.removeStorageSync('isLogin');
										uni.removeStorageSync('accessToken');
										uni.removeStorageSync('currentUser');
										uni.removeStorageSync('platform');

										// #ifdef H5
										this.$alert('退出登录成功', 'success', '/pages/article/index');
										// #endif

										// #ifndef H5
										this.$alert('退出登录成功', 'success', '/pages/article/index', 'switchTab');
										// #endif

									} else {
										this.$alert(res.msg, 'warning');
									}
								},
								complete: function() {
									uni.hideLoading();
								}
							});
						}
					}
				});
			}
		}
	};
</script>

<style scoped lang="scss">
	.top {

		/*页面标题*/
		.page-title {
			display: flex;
			justify-content: center;
			align-items: center;
			height: 90rpx;
			text-align: center;
			z-index: 9999;
			font-size: 38rpx;
			font-weight: bold;
			color: #ffffff;
		}
	}

	.user {
		.base-info {
			//background-image: linear-gradient(top, #e4edec 20%, #e4edec 80%);
			background: #ffd100;
			height: 280rpx;
		}

		.user-info {
			padding: 64rpx 32rpx;
			display: flex;
			align-item: center;
			justify-content: space-between;

			.avatar {
				flex-grow: 0;
				flex-shrink: 0;
				line-height: 0;
				border-radius: 50%;

				image {
					width: 144rpx;
					height: 144rpx;
					border-radius: 50%;
					border: 1rpx solid #fff;
				}
			}

			.default-avatar {
				margin-top: -12rpx;
			}

			.info {
				flex-grow: 1;
				flex-shrink: 1;
				padding-left: 28rpx;
				display: flex;
				flex-direction: column;
				justify-content: center;

				.nickname {
					font-size: 44rpx;
					color: #ffffff;
					margin-top: -14rpx;
				}

				.mobile {
					font-size: 34rpx;
					color: #fff;
					display: flex;
					align-items: center;
					line-height: normal;

					/deep/ .icon {
						margin-left: 30rpx;
						font-size: 28rpx;
						display: block;
					}
				}

				.user-type {
					display: flex;
					margin-top: 6rpx;

					text {
						font-size: 24rpx;
						color: #fff;
						background-color: #cd8202;
						padding: 0 24rpx;
						border-radius: 55px;
					}
				}

				.btn-login {
					font-size: 42rpx;
					background: transparent;
					color: #fff;
					text-align: left;
					width: 200rpx;
				}
			}

			.setting {
				flex-grow: 0;
				flex-shrink: 0;

				.btn-setting {
					margin-top: 10rpx;

					/deep/ .icon {
						color: #fff;
						font-size: 48rpx;
					}
				}
			}
		}
	}

	.nav {
		margin-top: -15rpx;
		margin-bottom: calc(var(--window-bottom) + 20rpx);

		.list {
			margin-top: 15rpx;
			margin-bottom: 20rpx;

			.item {
				padding-left: 24rpx;
				background: #ffffff;

				.info {
					display: flex;
					flex-direction: row;
					align-items: center;
					justify-content: space-between;
					border-bottom: 1rpx solid #e9ebee;
					padding: 4rpx 24rpx 4rpx 0;
					height: 100rpx;

					.name {
						display: flex;
						flex-direction: row;
						align-items: center;
						justify-content: center;
						font-size: 34rpx;
						line-height: normal;

						image {
							width: 45rpx;
							height: 45rpx;
							margin-right: 16rpx;
						}

						/deep/ .icon {
							color: #ffd100;
							font-size: 44rpx;
							margin-right: 16rpx;
							line-height: normal;
						}

						/deep/ .icon-car {
							font-size: 35rpx;
							margin-right: 14rpx;
						}

						/deep/ .icon-profile-o {
							font-size: 48rpx;
							margin-right: 12rpx;
						}

						/deep/ .icon-user-info {
							font-size: 50rpx;
							margin-left: -5rpx;
							margin-right: 10rpx;
						}

						/deep/ .icon-favorite {
							font-size: 42rpx;
						}

						/deep/ .icon-about {
							font-size: 44rpx;
							margin-left: -4rpx;
							margin-right: 12rpx;
						}

						/deep/ .icon-question {
							margin-right: 16rpx;
						}
					}

					/deep/ .icon {
						color: #c1c4c9;
						font-size: 24rpx;
					}
				}

				&:last-child {
					.info {
						border-bottom: 0;
					}
				}
			}
		}
	}

	.status-bar {
		background: #ffd100;
	}
</style>
