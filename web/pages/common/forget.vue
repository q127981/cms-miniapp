<template>
	<view class="page">
		<view class="content">
			<view class="list">
				<view class="item">
					<iconfont type="mobile"></iconfont>
					<input class="input" v-model="mobile" type="number" maxlength="11" placeholder="请输入手机号" />
				</view>
				<view class="item verify-item">
					<view class="verfiy-code-icon"><iconfont type="verify-code"></iconfont></view>
					<input class="input" v-model="verify_code" type="text" maxlength="4" placeholder="请输入验证码" />
					<view class="verify-btn" @tap="getVerifyCode">{{ verify_text }}</view>
				</view>
				<view class="item">
					<iconfont type="pwd"></iconfont>
					<input class="input" v-model="password" type="text" maxlength="32" placeholder="请输入新密码" password="true" />
				</view>
			</view>
			<view class="button" hover-class="button-hover" @tap="updatePassword"><text>提交</text></view>
		</view>
	</view>
</template>

<script>
import { mapState, mapMutations } from 'vuex';
import iconfont from '@/components/iconfont/iconfont.vue';
import validator from '@/common/validator';
var that, js;
export default {
	components: {
		iconfont
	},
	onShow() {
		this.$initPageTitle();//初始化页面标题
	},
	onLoad() {
		that = this;
	},
	onUnload() {
		clearInterval(js);
		this.second = 0;
	},
	data() {
		return {
			mobile: '',
			password: '',
			verify_code: '',
			second: 0
		};
	},
	computed: {
		verify_text() {
			if (this.second == 0) {
				return '获取验证码';
			} else {
				if (this.second < 10) {
					return '0' + this.second + '秒后重新获取';
				} else {
					return this.second + '秒后重新获取';
				}
			}
		}
	},
	methods: {
		/*获取验证码*/
		getVerifyCode() {
			if (this.second > 0) {
				return;
			}
			if (!validator.checkMobile(this.mobile)) {
				return;
			}
			this.second = 60;
			js = setInterval(function() {
				that.second--;
				if (that.second == 0) {
					clearInterval(js);
				}
			}, 1000);
			this.$app.request({
				url: this.$api.common.verify,
				data: {
					mobile: this.mobile,
					type: 'normal'
				},
				method: 'POST',
				dataType: 'json',
				success: res => {
					if (res.code != 0) {
						this.$alert(res.msg);
					}
				}
			});
		},

		/*重置密码*/
		updatePassword() {
			if (!validator.checkMobile(this.mobile)) {
				return;
			} else if (!validator.checkNewPassword(this.password)) {
				return;
			} else if (!validator.checkVerifyCode(this.verify_code)) {
				return;
			}
			uni.showLoading({
				title: '提交中…'
			});
			this.$app.request({
				url: this.$api.user.forgot,
				data: {
					mobile: this.mobile,
					new_password: this.password,
					verify_code: this.verify_code
				},
				method: 'POST',
				dataType: 'json',
				success: res => {
					console.log(res);
					if (res.code == 0) {
						uni.hideLoading();
						this.$alert('密码重置成功');

						/*跳转到登录页*/
						setTimeout(() => {
							uni.navigateTo({
								url: '/pages/common/login'
							});
						}, 1000);
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
	margin-top:50rpx;
	.logo {
		margin-top: 143rpx;
		margin-left: auto;
		margin-right: auto;

		image {
			width: 218rpx;
			height: 219rpx;
		}
	}

	.list {
		display: flex;
		flex-direction: column;
		padding-top: 40rpx;
		padding-left: 100rpx;
		padding-right: 100rpx;
		padding-bottom: 40rpx;

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

			.verfiy-code-icon {
				.icon {
					font-size: 52rpx;
					margin-right: -6rpx;
					margin-left: -4rpx;
				}
			}

			.input {
				flex: 1;
				text-align: left;
				font-size: 30rpx;
				margin-left: 16rpx;
			}
		}

		.verify-item {
			position: relative;

			.verify-btn {
				position: absolute;
				top: 22rpx;
				right: 10rpx;
				color: #ffd100;
				font-size: 30rpx;
				z-index: 9999;
			}
		}
	}

	.protocol {
		display: flex;
		flex-direction: row;
		align-items: center;
		margin-top: 65rpx;
		padding-left: 100rpx;
		text-align: left;

		.agree-box {
			width: 55rpx;
		}

		.name {
			margin-left: 5rpx;
		}
	}

	.other {
		display: flex;
		flex-direction: row;
		justify-content: center;
		align-items: center;
		font-size: 30rpx;
		margin-top: 30rpx;
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
