<template xlang="wxml" minapp="mpvue">
	<view class="_imageCompress">
		<canvas id="_myCanvas" canvas-id="_myCanvas" :style="{width:cWidth+'px',height:cHeight+'px'}" />
	</view>
</template>

<script>
let _rtArr = [], _cgFile = '', _index = 0;
export default {
	name: "imageCompress",
	props: {
		maxWidth: {
			type: Number,
			default: 750
		},
		type: {
			type: String,
			default: 'url'
		},
		ql: {
			type: Number,
			default: 0.92
		},
		src: {
			type: String,
			default: ''
		},
		number: {
			type: Number,
			default: 1
		},
		fixOrientation: {
			type: Boolean,
			default: true
		},
		size: {
			type: Number,
			default: 500000
		},
	},
	data() {
		return {
			cWidth: 750,
			cHeight: 750,
		}
	},
	onUnload: function () {
	},
	methods: {
		// 选择照片
		_changImg() {
			let that = this;
			if (that.src == '') {
				uni.chooseImage({
					count: that.number, //默认9
					sizeType: ['compressed'], //可以指定是原图还是压缩图，默认二者都有
					// sizeType: ['original', 'compressed'], //可以指定是原图还是压缩图，默认二者都有
					success: function (res) {
						_rtArr = [];
						_cgFile = res.tempFilePaths;
						_index = 0;
						that._imageCompress()
					},
					fail: function (e) {
						that._err(e)
						// console.log(JSON.stringify(res.tempFilePaths));
					}
				});
			} else {
				_imageCompress(that.src)
			}
		},
		// 压缩图片
		_imageCompress() {
			let that = this, resPath = _cgFile[_index];
			// #ifdef APP-PLUS
			uni.hideLoading();
			// #endif
			uni.showLoading({
				title: `图片压缩中 ${_index + 1}/${_cgFile.length}`,
				mask: true
			});
			// 获取图片信息
			// #ifndef H5
			uni.getImageInfo({
				src: resPath,
				success: function (image) {
					// 如果图片的大小大于设定值才压缩
					uni.getFileInfo({
						filePath: resPath,
						success: function (res) {
							_imageCompress(image, res.size)
						},
						fail: function (e) {
							that._err(e)
						}
					})
				},
				fail: function (e) {
					that._err(e)
				}
			});
			// #endif
			// #ifdef H5
			that._getH5ImageInfo(resPath, d => {
				_imageCompress(d, d.size)
			})
			// #endif 
			// 处理图片
			function _imageCompress(image, size) {
				// #ifndef APP-PLUS
				if (image.type.indexOf('png') >= 0) {
					that._result(resPath)
					return false
				}
				// #endif
				let oW = image.width, oH = image.height, scaleWidth = 1, scaleHeight = 1, ctxWidth, ctxHeight;
				if (size / 1024 >= that.size || image.width >= that.maxWidth) {
					// 控制上传图片的宽高
					if (image.width >= image.height && image.width >= that.maxWidth) {
						image.height = that.maxWidth * image.height / image.width;
						image.width = that.maxWidth;
					} else if (image.width < image.height && image.height >= that.maxWidth) {
						image.width = that.maxWidth * image.width / image.height;
						image.height = that.maxWidth;
					}
					scaleWidth = image.width / oW
					scaleHeight = image.height / oH
				}
				ctxWidth = oW * scaleWidth
				ctxHeight = oH * scaleHeight
				const ctx = uni.createCanvasContext('_myCanvas', that);
				that.cWidth = image.width;
				that.cHeight = image.height;
				// 图片旋转修正，只有H5有效
				if (that.fixOrientation) {
					// 旋转图片
					let ot = image.orientation
					if ([5, 6, 7, 8, 'right', 'left', 'right-mirrored', 'left-mirrored'].indexOf(ot) > -1) {
						that.cWidth = image.height;
						that.cHeight = image.width;
					}
					// 代码参考 https://stackoverflow.com/questions/19463126/how-to-draw-photo-with-correct-orientation-in-canvas-after-capture-photo-by-usin
					if (ot == 2 || ot == "up-mirrored") {
						ctx.translate(ctxWidth, 0);
						ctx.scale(-1, 1);
					} else if (ot == 3 || ot == "down") {
						ctx.translate(ctxWidth, ctxHeight);
						ctx.rotate(Math.PI);
					} else if (ot == 4 || ot == "down-mirrored") {
						ctx.translate(0, ctxHeight);
						ctx.scale(1, -1);
					} else if (ot == 5 || ot == "right-mirrored") {
						ctx.rotate(0.5 * Math.PI);
						ctx.scale(1, -1);
					} else if (ot == 6 || ot == "right") {
						ctx.rotate(0.5 * Math.PI);
						ctx.translate(0, -ctxHeight);
					} else if (ot == 7 || ot == "left-mirrored") {
						ctx.rotate(0.5 * Math.PI);
						ctx.translate(ctxWidth, -ctxHeight);
						ctx.scale(-1, 1);
					} else if (ot == 8 || ot == "left") {
						ctx.rotate(-0.5 * Math.PI);
						ctx.translate(-ctxWidth, 0);
					} else {
						ctx.translate(0, 0);
					}
				}
				let ctxTime = 0;
				// #ifndef H5
				ctxTime = 10;
				// #endif
				// #ifdef H5
				ctxTime = cTime();
				// #endif
				setTimeout(() => {
					ctx.drawImage(resPath, 0, 0, ctxWidth, ctxHeight)
					ctx.draw(false, () => {
						let time = 0;
						// #ifndef MP-WEIXIN
						time = 10;
						// #endif
						// #ifdef MP-WEIXIN
						time = cTime();
						// #endif
						setTimeout(() => {
							uni.canvasToTempFilePath({
								width: Number(that.cWidth),
								height: Number(that.cHeight),
								destWidth: Number(that.cWidth),
								destHeight: Number(that.cHeight),
								canvasId: '_myCanvas',
								fileType: 'jpg',
								quality: Number(that.ql),
								success: function (res) {
									if (that.type == 'base64') {
										let img = '';
										//#ifdef MP-WEIXIN
										img = 'data:image/jpeg;base64,' + wx.getFileSystemManager().readFileSync(res.tempFilePath, "base64")
										that._result(img)
										//#endif
										//#ifdef APP-PLUS
										// console.log(JSON.stringify(res))
										plus.io.resolveLocalFileSystemURL(res.tempFilePath, function (entry) {
											entry.file(function (file) {
												let fileReader = new plus.io.FileReader();
												// console.log("getFile:" + JSON.stringify(file));
												fileReader.readAsDataURL(file);
												fileReader.onloadend = function (evt) {
													// console.log(JSON.stringify(evt))
													if (evt.target.readyState == 2) {
														that._result(evt.target.result)
													} else {
														that._err(evt)
													}
												}
											});
										}, function (e) {
											that._err(e)
										});
										//#endif
										//#ifdef H5
										that._result(res.tempFilePath)
										//#endif
									} else {
										that._result(res.tempFilePath)
									}
								},
								fail: function (e) {
									that._err(e)
								}
							}, that)

						}, time);
					});
				}, ctxTime);
				function cTime() {
					let time = that.maxWidth / 5
					if (time >= 600) {
						return 600
					} else if (time <= 100) {
						return 100
					} else {
						return time
					}
				}
			}
		},
		// ios翻转图片
		_reverseImgData(res) {
			let w = res.width
			let h = res.height
			let con = 0
			for (let i = 0; i < h / 2; i++) {
				for (let j = 0; j < w * 4; j++) {
					con = res.data[i * w * 4 + j]
					res.data[i * w * 4 + j] = res.data[(h - i - 1) * w * 4 + j]
					res.data[(h - i - 1) * w * 4 + j] = con
				}
			}
			return res
		},
		_getH5ImageInfo(url, callback) {
			let image = new Image()
			image.src = url
			image.onload = function (d) {
				let imgSelf = this
				let http = new XMLHttpRequest();
				http.open("GET", url, true);
				http.responseType = "blob";
				http.onload = function (e) {
					let httpSelf = this
					if (httpSelf.status == 200 || httpSelf.status === 0) {
						let reader = new FileReader();
						reader.onload = function (e) {
							// 代码参考 https://www.jianshu.com/p/eb855b580780
							let view = new DataView(this.result);
							if (view.getUint16(0, false) != 0xFFD8) return callback({ size: httpSelf.response.size, type: httpSelf.response.type, width: imgSelf.width, height: imgSelf.height, orientation: -1 });
							let length = view.byteLength, offset = 2;
							while (offset < length) {
								let marker = view.getUint16(offset, false);
								offset += 2;
								if (marker == 0xFFE1) {
									if (view.getUint32(offset += 2, false) != 0x45786966) return callback({ size: httpSelf.response.size, type: httpSelf.response.type, width: imgSelf.width, height: imgSelf.height, orientation: -1 });
									let little = view.getUint16(offset += 6, false) == 0x4949;
									offset += view.getUint32(offset + 4, little);
									let tags = view.getUint16(offset, little);
									offset += 2;
									for (let i = 0; i < tags; i++)
										if (view.getUint16(offset + (i * 12), little) == 0x0112)
											return callback({ size: httpSelf.response.size, type: httpSelf.response.type, width: imgSelf.width, height: imgSelf.height, orientation: view.getUint16(offset + (i * 12) + 8, little) });
								}
								else if ((marker & 0xFF00) != 0xFF00) break;
								else offset += view.getUint16(offset, false);
							}
							return callback({ size: httpSelf.response.size, type: httpSelf.response.type, width: imgSelf.width, height: imgSelf.height, orientation: -1 });
						};
						reader.readAsArrayBuffer(httpSelf.response);
					}
				};
				http.send();
			}
		},
		// 返回图片数据
		_result(src, index) {
			_rtArr.push(src)
			_index = _index + 1;
			if (_cgFile.length - 1 >= _index) {
				this._imageCompress()
			} else {
				uni.hideLoading();
				this.$emit("result", _rtArr);
			}
		},
		_err(src) {
			uni.hideLoading();
			this.$emit("err", src);
		}
	}
}
</script>

<style>
._imageCompress {
	position: fixed;
	top: -99999upx;
	left: -99999upx;
	z-index: -99999;
}
</style>
