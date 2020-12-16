<template>
	<view class="page">
		<view class="status-bar"></view>
		<view class="content">
			<view class="logo" v-if="false">
				<image :lazy-load="true" src="../../static/images/logo.png"></image>
			</view>
			<view class="list">
				<view class="item">
					<iconfont type="mobile"></iconfont>
					<input class="input" v-model="mobile" type="number" maxlength="11" placeholder="请输入手机号" />
				</view>
				<view class="item">
					<iconfont type="user"></iconfont>
					<input class="input" v-model="nickname" type="text" maxlength="32" placeholder="请输入昵称" />
				</view>
				<view class="item">
					<iconfont type="pwd"></iconfont>
					<input class="input" v-model="password" type="text" maxlength="32" placeholder="请输入密码" password="true" />
				</view>
				<view class="item verify-item">
					<view class="verfiy-code-icon">
						<iconfont type="verify-code"></iconfont>
					</view>
					<input class="input" v-model="verify_code" type="text" maxlength="4" placeholder="请输入验证码" />
					<view class="verify-btn" @tap="getVerifyCode">{{verify_text}}</view>
				</view>
			</view>
			<view class="protocol">
				<view class="agree-box">
					<checkbox-group @change="agreeChange">
						<label>
							<checkbox value="1" v-model="agree" name="agree" checked="true" />
						</label>
					</checkbox-group>
				</view>
				<navigator url="/pages/common/protocol">
					<view class="name">同意用户协议和隐私政策</view>
				</navigator>
			</view>
			<view class="button" hover-class="button-hover" @tap="register">
				<text>注册</text>
			</view>
			<view class="other">
				<navigator url="login?type=app" open-type="navigate">已有账号，点此去登录</navigator>
			</view>
		</view>
	</view>
</template>

<script>
	import { mapState, mapMutations } from 'vuex';
	import iconfont from '@/components/iconfont/iconfont.vue'
	import validator from '@/common/validator'
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
			clearInterval(js)
			this.second = 0;
		},
		data() {
			return {
				mobile: '',
				nickname: '',
				password: '',
				verify_code: '',
				agree: '1',
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
			...mapMutations(['login']),
			/*是否同意选择*/
			agreeChange(e) {
				if (e.detail.value.length > 0) {
					this.agree = '1';
				} else {
					this.agree = '0';
				}
			},

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
						clearInterval(js)
					}
				}, 1000)
				this.$app.request({
					url: this.$api.common.verify,
					data: {
						mobile: this.mobile,
						type: 'normal'
					},
					method: 'POST',
					dataType: 'json',
					success: (res) => {
						if (res.code != 0) {
							this.$alert(res.msg);
						}
					}
				});
			},

			/*注册*/
			register() {
				if (!validator.checkMobile(this.mobile)) {
					return;
				} else if (!validator.checkNickname(this.nickname)) {
					return;
				} else if (!validator.checkPassword(this.password)) {
					return;
				} else if (!validator.checkVerifyCode(this.verify_code)) {
					return;
				}
				uni.showLoading({
					title: '注册中…'
				})
				this.$app.request({
					url: this.$api.user.register,
					data: {
						mobile: this.mobile,
						nickname: this.nickname,
						password: this.password,
						verify_code: this.verify_code
					},
					method: 'POST',
					dataType: 'json',
					success: (res) => {
						console.log(res)
						if (res.code == 0) {
							uni.hideLoading();
							this.$alert('注册成功');
							
							/*更新登录状态,保存用户数据*/
							this.login(res.data);
							
							/*跳转首页*/
							setTimeout(() => {
								uni.switchTab({
									url: '/pages/user/index'
								})
							}, 1500)
						} else {
							this.$alert(res.msg);
						}
					}
				});
			}
		}
	}
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
			padding-top: 40rpx;
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
