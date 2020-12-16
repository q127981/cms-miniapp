<template>
	<view class="container">
		<view class="content">
			<view class="login">
				<block v-if="canIUse">
					<view class="photo"><image src="/static/images/default_user_photo.jpg" class="avatar"></image></view>
					<view class="auth">
						<button class="button" type="primary" open-type="getUserInfo" @getuserinfo="bindGetuserInfo">微信登录</button>
						<navigator class="btn-cancel" url="/pages/article/index" open-type="switchTab">取消</navigator>
<!-- 						<navigator v-if="!hasAuth" class="btn-cancel" url="/pages/article/index" open-type="switchTab">取消</navigator> -->
					</view>
				</block>
				<block v-else><view class="upgrade">当前微信版本太低不支持该小程序部分功能，请升级微信版本</view></block>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			hasAuth: true,
			canIUse: true
		};
	},
	onLoad() {
		this.canIUse = uni.canIUse('button.open-type.getUserInfo');
		/*是否授权*/
		uni.getSetting({
			success: res => {
				this.hasAuth = false;
				/*已经授权直接登录*/
				// if (res.authSetting['scope.userInfo']) {
				// 	this.$app.wechatAppLogin(true); //登录
				// } else {
				// 	this.hasAuth = false;
				// }
			}
		});
	},
	methods: {
		bindGetuserInfo() {
			this.$app.wechatAppLogin(true); //登录
		}
	}
};
</script>

<style lang="scss" scoped>
.login {
	view {
		padding: 50rpx;
		text-align: center;
		image {
			width: 250rpx;
			height: 250rpx;
		}
	}
	.auth {
		.btn-cancel {
			margin-top: 20rpx;
			border-radius: 100px;
			font-size: 32rpx;
			color: #999;
		}
	}

	.upgrade {
		font-size: 30rpx;
	}
}

button::after {
	border: 0;
}
</style>
