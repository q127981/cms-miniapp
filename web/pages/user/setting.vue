<template>
	<view class="wrap">
		<view class="content">
			<view class="list">
				<navigator class="item" url="profile" hover-class="none">
					<view class="name">
						<iconfont type="profile-o"></iconfont>
						<text>用户资料</text>
					</view>
					<view class="go"><iconfont type="go"></iconfont></view>
				</navigator>
				<!-- #ifndef MP-WEIXIN  -->
				<navigator class="item" url="password" hover-class="none">
					<view class="name">
						<iconfont type="password-o"></iconfont>
						<text>密码修改</text>
					</view>
					<view class="go"><iconfont type="go"></iconfont></view>
				</navigator>
				<!-- #endif -->
				<view class="item" @tap="bindLogout">
					<view class="name">
						<iconfont type="logout-o"></iconfont>
						<text>退出登录</text>
					</view>
					<view class="go"><iconfont type="go"></iconfont></view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { mapState, mapMutations } from 'vuex';
import iconfont from '@/components/iconfont/iconfont.vue';

export default {
	components: {
		iconfont
	},
	data() {
		return {};
	},
	onShow() {
		this.$initPageTitle(); //初始化页面标题
		if (!this.$app.isLogin()) {
			this.$app.login();
		}
	},
	onLoad(e) {},
	onPullDownRefresh() {},
	computed: mapState(['isLogin']),
	methods: {
		...mapMutations(['logout']),

		/*退出登录*/
		bindLogout() {
			var that = this;
			uni.showModal({
				title: '提示',
				content: '确认退出登录?',
				success: function(res) {
					if (res.confirm) {
						uni.showLoading({
							title: '退出登录中…'
						});
						that.$app.request({
							url: this.$api.user.logout,
							method: 'POST',
							dataType: 'json',
							success: res => {
								console.log(res);
								if (res.code == 0) {
									that.logout(); //退出登录
									// #ifdef MP
									uni.switchTab({
										url: '/pages/article/index'
									});
									// #endif
									// #ifndef MP
									// #ifdef H5
									uni.navigateTo({
										url: '/pages/user/index'
									});
									// #endif
									// #ifndef H5
									uni.switchTab({
										url: '/pages/user/index'
									});
									// #endif
									// #endif
								} else {
									that.$alert(res.msg);
								}
							},
							complete: res => {
								uni.stopPullDownRefresh();
								uni.hideLoading();
							}
						});
					} else if (res.cancel) {
						console.log('用户点击取消');
					}
				}
			});
		}
	}
};
</script>

<style scoped lang="scss">
.list {
	padding-left: 24rpx;
	background: #ffffff;
	.item {
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: space-between;
		padding: 10rpx 30rpx 10rpx 0;
		height: 76rpx;
		border-bottom: 1rpx solid #e9ebee;
		&:last-child {
			border-bottom: 0;
		}
		.name {
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: center;
			font-size: 32rpx;
			text {
				color: #000000;
				margin-left: 20rpx;
			}

			/deep/.icon {
				font-size: 36rpx;
				color: #ffd100;
			}
		}

		.go {
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: center;

			/deep/.icon {
				color: #dddddd;
				font-size: 20rpx;
			}
		}
	}
}
</style>
