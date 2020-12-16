<template xlang="wxml" minapp="mpvue">
	<view class="imageCompress">
		<view class="photo" v-if="result.length > 0">
			<view class="item" v-for="(item, index) in result" :key="index">
				<image :src="item.url" @tap="previewPhoto(item.url)" mode="widthFix"></image>
				<view class="remote-photo-tag" @tap="removePhoto(index)"><image src="/static/images/icon-remove.png"></image></view>
			</view>
		</view>
		<view class="upload" v-if="result.length < limit">
			<view class="info" @tap="upload()">
				<image src="/static/images/icon-photo.png"></image>
				<text>{{ tip }}</text>
				<text v-if="remark" class="remark">{{ remark }}</text>
			</view>
		</view>
		<canvas
			class="canvas"
			:canvas-id="canvasId"
			:style="'width:' + destWidth + 'px;height:' + destHeight + 'px;top:' + -canvasIndex * 9000 + 'px;left:' + -canvasIndex * 9000 + 'px'"
		></canvas>
	</view>
</template>
<script>
export default {
	name: 'imageCompress',
	props: {
		/*图片类型*/
		type: {
			type: String,
			default: 'water'
		},
		/*上传限制*/
		limit: {
			type: Number,
			default: 1
		},
		/*最大允许宽度*/
		maxWidth: {
			type: Number,
			default: 750
		},
		/*默认图片url（单张）*/
		src: {
			type: String,
			default: ''
		},
		/*默认图片url（多张）*/
		list: {
			type: Array,
			default: () => {
				return [];
			}
		},
		/*上传提示*/
		tip: {
			type: String,
			default: ''
		},
		/*上传备注*/
		remark: {
			type: String,
			default: ''
		},
		/*是否上传至服务端*/
		isUploadServer: {
			type: Boolean,
			default: true
		},
		/*服务端url*/
		serverUrl: {
			type: String,
			default: ''
		}
	},
	data() {
		return {
			result: [],
			uploadPhoto: [],
			photoIndex: 0,
			uploadIndex: 1,
			canvasIndex: 1,
			canvasId: 'canvas',
			destWidth: 750,
			destHeight: 750
		};
	},
	created() {
		/*默认值初始化*/
		this.initDefaultValue();

		// #ifdef H5
		this.canvasId = Date.now().toString(36);
		// #endif
	},
	watch: {
		src: function(newVal, oldVal) {
			if (this.result.length == 0 && this.src) {
				console.log('refresh');
				this.src = newVal;
				this.initDefaultValue();
			}
		},
		list: function(newVal, oldVal) {
			if (this.result.length == 0 && this.list.length > 0) {
				this.list = newVal;
				this.initDefaultValue();
			}
		},
		tip: function(newVal, oldVal) {
			this.tip = newVal;
		}
	},
	methods: {
		/*初始化默认值*/
		initDefaultValue() {
			if (this.result.length == 0) {
				/*单张*/
				if (this.src) {
					let data = { url: this.src };
					this.result.push(data);
				}

				/*多张*/
				if (this.list.length > 0) {
					this.list.forEach(item => {
						let data = { url: item };
						this.result.push(data);
					});
				}
			}
		},
		/*上传图片*/
		upload() {
			let maxUploadCount = this.limit;
			if (this.limit > 1) {
				maxUploadCount = this.limit - this.result.length;
			}
			uni.chooseImage({
				count: maxUploadCount,
				sizeType: ['compressed'], // 指定只能为压缩图，首先进行一次默认压缩
				sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
				success: res => {
					this.uploadPhoto = res.tempFilePaths;
					this.uploadIndex = 0;
					if (this.limit > 1 && this.uploadPhoto.length > maxUploadCount) {
						if (this.result.length > 0) {
							this.$alert('只能再上传' + maxUploadCount + '张', 'warning');
						} else {
							this.$alert('最多只能上传' + maxUploadCount + '张', 'warning');
						}
						return;
					}
					if (this.limit == 1) {
						this.result = [];
						this.$loading('图片上传…');
					} else {
						this.initLoading();
					}
					this.compress(this.uploadPhoto[this.uploadIndex]);
				}
			});
		},

		/*压缩图片*/
		compress(photo) {
			uni.getImageInfo({
				src: photo,
				success: imageInfo => {
					let canvasWidth = imageInfo.width;
					let canvasHeight = imageInfo.height;
					while (canvasWidth > this.maxWidth) {
						canvasWidth = this.maxWidth;
						canvasHeight = Math.trunc((imageInfo.height / imageInfo.width) * this.maxWidth);
					}
					this.destWidth = canvasWidth;
					this.destHeight = canvasHeight;

					/*绘制时间*/
					let ctxTime = 10;
					// #ifdef H5
					ctxTime = this.canvasTime();
					// #endif

					/*绘制类型*/
					let fileType = imageInfo.path.replace(/^.+\./, '');
					if (fileType == 'png') {
						fileType = 'png';
					} else {
						fileType = 'jpg';
					}

					/*绘制*/
					let ctx = wx.createCanvasContext(this.canvasId, this);
					setTimeout(() => {
						ctx.drawImage(imageInfo.path, 0, 0, this.destWidth, this.destHeight);
						ctx.draw(false, () => {
							/*绘制时间*/
							let drawTime = 10;
							// #ifdef MP
							drawTime = this.canvasTime();
							// #endif
							setTimeout(() => {
								uni.canvasToTempFilePath(
									{
										x: 0,
										y: 0,
										width: this.destWidth,
										height: this.destHeight,
										destWidth: this.destWidth,
										destHeight: this.destHeight,
										fileType: fileType,
										canvasId: this.canvasId,
										success: res => {
											this.submit(res.tempFilePath);

											/*上传多张*/
											if (this.uploadPhoto.length - 1 != this.uploadIndex) {
												this.uploadIndex = this.uploadIndex + 1;
												this.initLoading();
												this.compress(this.uploadPhoto[this.uploadIndex]);
											}
										},
										fail: err => {
											console.log(err);
										},
										complete: res => {
											this.canvasIndex += 1;
											// #ifdef H5
											this.canvasId = Date.now().toString('36');
											// #endif
										}
									},
									this
								);
							}, drawTime);
						});
					}, ctxTime);
				}
			});
		},

		/*绘制时间*/
		canvasTime() {
			let time = this.maxWidth / 2;
			if (time >= 600) {
				return 600;
			} else if (time <= 100) {
				return 100;
			} else {
				return time;
			}
		},

		/*刷新加载提示*/
		initLoading() {
			if (this.uploadPhoto.length > 1) {
				let loadingTxt = '图片上传（' + (this.uploadIndex + 1) + '/' + this.uploadPhoto.length + '）';
				this.$loading(loadingTxt);
			}
		},

		/*上传提交*/
		submit(photoUrl) {
			/*不上传服务端*/
			if (!this.isUploadServer) {
				let data = { url: photoUrl };
				this.result.push(data);
				this.$emit('upload', data);
				if (this.uploadPhoto.length - 1 == this.uploadIndex) {
					uni.hideLoading();
				}
				return;
			}

			/*默认上传url*/
			let serverUrl = this.serverUrl;
			if (serverUrl == '') {
				serverUrl = this.$api.user.upload;
			}

			/*上传至服务端*/
			this.$app.uploadFile({
				url: serverUrl,
				filePath: photoUrl,
				name: 'file',
				formData: { type: this.type },
				success: res => {
					if (res.code == 0) {
						let data = { url: res.data.url, detail: res.data };
						this.result.push(data);
						console.log(this.result);
						this.$emit('upload', data);
					} else {
						this.$alert(res.msg, 'warning');
					}
				},
				complete: res => {
					if (this.uploadPhoto.length - 1 == this.uploadIndex) {
						uni.hideLoading();
					}
				}
			});
		},

		/*删除图片*/
		removePhoto(index) {
			this.photoIndex -= 1;
			this.result.splice(index, 1);
			this.$emit('remove', index);
		},

		/*预览图片*/
		previewPhoto(photoUrl) {
			uni.previewImage({
				current: 0,
				urls: [photoUrl]
			});
		}
	}
};
</script>

<style lang="scss" scoped>
.imageCompress {
	.photo {
		display: flex;
		flex-direction: column;
		margin: 0 30rpx;
		.item {
			position: relative;
			margin: 34rpx 0 0;
			width: 100%;
			line-height: 0;
			&:first-child {
				margin-top: 20rpx;
			}
			&:last-child {
				margin-bottom: 20rpx;
			}
			image {
				width: 100%;
			}
			.remote-photo-tag {
				position: absolute;
				top: -20rpx;
				right: -20rpx;
				z-index: 100;
				image {
					width: 40rpx;
					height: 40rpx;
				}
			}
		}
	}
	.upload {
		display: flex;
		justify-content: center;
		.info {
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			width: 500rpx;
			height: 340rpx;
			border: 1rpx dashed #026836;
			border-radius: 10rpx;
			margin-top: 34rpx;
			padding: 15rpx 0;
			&:first-child {
				margin-top: 20rpx;
			}
			&:last-child {
				margin-bottom: 20rpx;
			}
			image {
				width: 100rpx;
				height: 100rpx;
			}
			text {
				padding: 15rpx 50rpx 0;
				line-height: normal;
				text-align: center;
			}
			.remark {
				color: #848c98;
				font-size: 24rpx;
			}
		}
	}
	.canvas {
		position: fixed;
		z-index: -9999;
	}
}
</style>
