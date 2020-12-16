<template>
	<view class="page">
		<view class="content">
			<view class="list">
				<view class="item">
					<iconfont type="pwd"></iconfont>
					<input class="input" v-model="old_password" type="text" maxlength="32" placeholder="请输入原密码" password="true" />
				</view>
				<view class="item">
					<iconfont type="pwd"></iconfont>
					<input class="input" v-model="new_password" type="text" maxlength="32" placeholder="请输入新密码" password="true" />
				</view>
				<view class="item">
					<iconfont type="pwd"></iconfont>
					<input class="input" v-model="confirm_new_password" type="text" maxlength="32" placeholder="请确认新密码" password="true" />
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
export default {
	components: {
		iconfont
	},
	onShow() {
		this.$initPageTitle();//初始化页面标题
	},
	onLoad() {},
	data() {
		return {
			old_password: '',
			new_password: '',
			confirm_new_password: ''
		};
	},
	methods: {
		/*修改密码*/
		updatePassword() {
			if (this.old_password=='') {
				this.$alert('请输入原密码');
				return;
			} else if (!validator.checkNewPassword(this.new_password)) {
				return;
			} else if (this.new_password!=this.confirm_new_password) {
				this.$alert('两次输入的新密码不一样');
				return;
			}
			uni.showLoading({
				title: '提交中…'
			});
			this.$app.request({
				url: this.$api.user.updatePassword,
				data: {
					old_password: this.old_password,
					new_password: this.new_password
				},
				method: 'POST',
				dataType: 'json',
				success: res => {
					console.log(res);
					if (res.code == 0) {
						uni.hideLoading();
						this.$alert('密码修改成功');

						/*后退*/
						setTimeout(() => {
							uni.navigateBack();
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
				color: #f9a853;
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
