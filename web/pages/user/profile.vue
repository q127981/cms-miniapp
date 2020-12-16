<template>
	<view class="container">
		<view class="content">
			<view class="list">
				<view v-if="!user.openid" class="item photo-item">
					<view class="name">
						<imageCropper @ok="confirm" @cancel="cancel" :fixed="true" :url="tempFilePath" :width="200" :height="200"></imageCropper>
						<image :src="cropFilePath" mode="aspectFill" @tap="upload"></image>
					</view>
					<view class="value" @tap="upload"><text class="tip">修改头像</text></view>
					<view class="go"><iconfont type="go"></iconfont></view>
				</view>
				<view v-else class="item photo-item">
					<view class="name"><image v-if="user.openid" class="avatar" :src="user.avatar_url"></image></view>
					<view class="value">
						<view class="nickname">{{ user.nickname }}</view>
					</view>
				</view>
				<view class="item" v-if="!user.openid">
					<view class="name">昵称</view>
					<view class="value">{{ user.nickname }}</view>
				</view>

				<view class="item" v-if="user.mobile">
					<view class="name">手机号码</view>
					<view class="value">{{ user.mobile }}</view>
				</view>
				<navigator class="item" v-if="!user.openid" url="password" hover-class="none">
					<view class="name">修改密码</view>
					<text class="value"></text>
					<view class="go"><iconfont type="go"></iconfont></view>
				</navigator>
				<view class="item" v-if="!user.mobile">
					<view class="name">手机号码</view>
					<view class="value"><input placeholder="请输入手机号码" v-model="mobile" type="number" maxlength="11" /></view>
				</view>
				<view class="item" v-if="!user.mobile">
					<view class="name">短信验证码</view>
					<input class="input" v-model="user.verify_code" placeholder-class="tip" type="text" maxlength="4" placeholder="请输入验证码" />
					<view class="btn-verify" @tap="getVerifyCode()">{{ verify_text }}</view>
				</view>
			</view>
			<view class="submit">
				<view class="button" hover-class="button-hover" @tap="submit()">保存</view>
				<view class="btn-text" @tap="logout()">退出登录</view>
			</view>
		</view>
		<pageLoading v-if="showPageLoading"></pageLoading>
	</view>
</template>

<script>
import util from '@/common/util.js';
import validator from '@/common/validator.js';
import pageLoading from '@/components/loading/pageLoading.vue';
import iconfont from '@/components/iconfont/iconfont.vue';
import imageCropper from '@/components/image/cropper.vue';
let timing;
export default {
	components: {
		pageLoading,
		iconfont,
		imageCropper
	},
	data() {
		return {
			mobile: '',
			second: 0,
			tempFilePath: '',
			cropFilePath: '',
			user: {},
			showPageLoading: true
		};
	},
	onShow() {
		this.$initPageTitle(); //初始化页面标题
		let source = uni.getStorageSync('source');
		if (source == 'login') {
			uni.removeStorageSync('source');
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
		}
	},
	onLoad(e) {
		this.loadData();
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
	onPullDownRefresh() {
		uni.showLoading({
			title: '刷新中'
		});
		this.loadData();
	},
	methods: {
		/*加载数据*/
		loadData() {
			this.getData();
		},

		/*获取数据*/
		getData() {
			this.$app.request({
				url: this.$api.user.userInfo,
				data: {},
				method: 'POST',
				dataType: 'json',
				success: res => {
					if (res.code == 0) {
						console.log(this.user);
						this.user = res.data;
						this.mobile = this.user.mobile;
						this.cropFilePath = this.user.avatar_url;
						this.showPageLoading = false;
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

		upload() {
			uni.chooseImage({
				count: 1, //默认9
				sizeType: ['original', 'compressed'], //可以指定是原图还是压缩图，默认二者都有
				sourceType: ['album'], //从相册选择
				success: res => {
					this.tempFilePath = res.tempFilePaths.shift();
				}
			});
		},
		confirm(e) {
			this.tempFilePath = '';
			this.cropFilePath = e.path;
			this.$loading('图片上传…');
			this.$app.uploadFile({
				url: this.$api.user.upload,
				filePath: e.path,
				name: 'file',
				formData: {},
				success: res => {
					console.log(res);
					console.log(res.code);
					if (res.code == 0) {
						this.user.avatar_id = res.data.id;
						this.user.avatar_url = res.data.url;
						this.cropFilePath = res.data.url;
					}
					uni.hideLoading();
				}
			});
		},
		cancel() {
			this.tempFilePath = '';
		},

		/*获取验证码*/
		getVerifyCode() {
			if (this.second > 0) {
				return;
			}
			if (!validator.checkMobile(this.user.mobile)) {
				return;
			}
			this.second = 60;
			timing = setInterval(() => {
				this.second--;
				if (this.second == 0) {
					clearInterval(timing);
				}
			}, 1000);
			this.$app.request({
				url: this.$api.user.verifyCode,
				data: {
					mobile: mobile,
					type: 'userSetting'
				},
				method: 'POST',
				dataType: 'json',
				success: res => {
					if (res.code != 0) {
						this.$alert(res.msg, 'warning');
					}
				}
			});
		},

		/*上传图片*/
		uploadPhoto() {
			uni.chooseImage({
				success: chooseImageRes => {
					const tempFilePaths = chooseImageRes.tempFilePaths;
					uni.showLoading({
						title: '上传中…'
					});
					console.log(tempFilePaths[0]);
					this.$app.uploadFile({
						url: this.$api.user.upload,
						filePath: tempFilePaths[0],
						name: 'file',
						formData: {},
						success: res => {
							console.log(res);
							console.log(res.code);
							if (res.code == 0) {
								this.user.avatar_id = res.data.id;
								this.user.avatar_url = res.data.url;
							}
							uni.hideLoading();
						}
					});
				}
			});
		},

		/*保存*/
		submit() {
			if (!this.mobile && !this.user.verify_code) {
				this.$alert('请输入短信验证码', 'warning');
				return false;
			}
			uni.showLoading({
				title: '提交中…'
			});
			let data = {
				mobile: this.mobile
			};
			if (this.user.avatar_url) {
				data.avatar_url = this.user.avatar_url;
			}
			if (this.user.verify_code) {
				data.verify_code = this.user.verify_code;
			}
			this.$app.request({
				url: this.$api.user.setting,
				data: data,
				method: 'POST',
				dataType: 'json',
				success: res => {
					if (res.code == 0) {
						this.$alert('保存成功', 'success');
					} else {
						this.$alert(res.msg, 'warning');
					}
				},
				complete: res => {
					uni.hideLoading();
				}
			});
		},

		/*退出登录*/
		logout() {
			uni.showModal({
				title: '提示',
				content: '确认退出登录？',
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
									uni.setStorageSync('source', 'logout');
									// #ifdef H5
									this.$alert('退出登录成功', 'success', '/pages/user/index');
									// #endif
									// #ifndef H5
									this.$alert('退出登录成功', 'success', '/pages/user/index', 'switchTab');
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

<style lang="scss" scoped>
.list {
	margin-top: 2rpx;
	padding-left: 24rpx;
	padding-right: 24rpx;
	border-bottom: 1rpx solid #eee;
	background: #fff;
	.item {
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: space-between;
		min-height: 76rpx;
		border-top: 1rpx solid #eee;
		padding: 10rpx 0;
		&:first-child {
			border: 0;
		}
		.name {
			flex-grow: 0;
			flex-shrink: 0;
			display: flex;
			flex-direction: row;
			align-items: center;
			font-size: 30rpx;
			width: 200rpx;
			color: #555;
			/deep/ .icon {
				color: #0b88ff;
				margin-right: 10rpx;
			}
			/deep/ .icon-mobile-01 {
				font-size: 38rpx;
				margin-right: 5rpx;
				margin-left: -5rpx;
			}
			/deep/ .icon-policy-file {
				font-size: 30rpx;
				margin-right: 14rpx;
				margin-left: -2rpx;
				margin-top: 2rpx;
			}
			text {
				color: #555;
			}
		}
		.value {
			flex-grow: 1;
			flex-shrink: 1;
			display: flex;
			flex-direction: row;
			align-items: center;
			font-size: 30rpx;
			width: 100%;
			justify-content: flex-start;
			text-align: left;
			/deep/.icon {
				margin-left: 20rpx;
				font-size: 20rpx;
				color: #c1c4c9;
			}
			input {
				padding-right: 30rpx;
				font-size: 30rpx;
				width: 100%;
			}
			image {
				width: 60rpx;
				height: 60rpx;
				border-radius: 50%;
				margin-right: 10rpx;
			}
			text {
				margin-right: 15rpx;
				text-align: left;
				line-height: normal;
				padding: 10rpx 0 10rpx 0;
			}
			.nickname {
				font-size: 30rpx;
			}
		}
		.btn-verify {
			display: flex;
			justify-content: center;
			align-items: center;
			border: 1rpx solid #848c98;
			background: #fff;
			height: 46rpx;
			line-height: 46rpx;
			padding: 0 20rpx;
			border-radius: 55rpx;
			font-size: 26rpx;
			color: #999;
			width: 220rpx;
		}
		.tip {
			font-size: 30rpx;
			margin-right: 15rpx;
			color: grey;
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
		.location {
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: center;
			/deep/.icon {
				color: grey;
			}
		}
		.drive-type {
			display: flex;
			align-items: center;
			.check-item {
				display: flex;
				align-items: center;
				margin-left: 30rpx;
				&:first-child {
					margin-left: 0;
				}
				.check-name {
					font-size: 30rpx;
					line-height: normal;
				}
			}
		}
	}
	.photo-item {
		height: 150rpx;
		image {
			width: 120rpx;
			height: 120rpx;
			border-radius: 50%;
		}
	}
	.textarea-item {
		padding-top: 10rpx;
		height: 110rpx;
		align-items: flex-start;
		.name {
			padding-top: 10rpx;
		}
		.value {
			textarea {
				padding-top: 12rpx;
				height: 100rpx;
				font-size: 30rpx;
				word-wrap: break-word;
				width: 524rpx;
				line-height: 1.5;
			}
		}
	}
}
.submit {
	padding: 30rpx 0 70rpx;
	background: #fff;
	.btn-text {
		display: flex;
		justify-content: center;
		margin-top: 24rpx;
		font-size: 32rpx;
		color: #ffd100;
	}
}
</style>
