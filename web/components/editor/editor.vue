<template>
	<view class="content">
		<!-- 工具栏 -->
		<view class="edit-tools">
			<view class="title">{{ title }}</view>
			<view class="operate">
				<view class="item" @tap="insertImg">
					<iconfont type="img"></iconfont>
					<text>插入图片</text>
				</view>
				<view class="item" @tap="insertVideo">
					<iconfont type="video"></iconfont>
					<text>插入视频</text>
				</view>
			</view>
		</view>
		<!-- 编辑区域 -->
		<scroll-view class="editor" :scroll-y="true" :scroll-with-animation="true" :scroll-into-view="scroll_editor_id">
			<view class="item" @tap="initTip()"  v-for="(item, index) in editItems" :key="index">
				<view class="editor-tip" v-if="editItems.length == 1 && showTip">{{ placeText }}</view>
				<textarea
					v-if="item.type == 'textarea'"
					:cursor-spacing="1"
					:maxlength="-1"
					:auto-height="true"
					:data-index="index"
					:show-confirm-bar="false"
					:fixed="false"
					:adjust-position="false"
					@input="inputIng"
					@linechange="lineChange"
					:focus="current_editor_id == index"
					:value="item.value"
				></textarea>
				<view class="img" v-if="item.type == 'img'">
					<image :src="item.value" mode="widthFix"></image>
					<view class="del" @click="delImg(index)"><image src="../../static/images/icon_delete.png"></image></view>
				</view>
				<view class="img video" v-if="item.type == 'video'">
					<video :src="item.value"></video>
					<view class="del" @click="delVideo(index)"><image src="../../static/images/icon_delete.png"></image></view>
				</view>
			</view>
			<view id="scorll_last" class="scorll-last"></view>
			<view id="scorll_end" class="scroll-end"></view>
		</scroll-view>
		<view class="edit-tools-flex"></view>
	</view>
</template>

<script>
import iconfont from '@/components/iconfont/iconfont.vue';
export default {
	name: 'editor',
	components: {
		iconfont
	},
	data() {
		return {
			showTip: true,
			current_editor_id: '-1',
			scroll_editor_id: '',
			pageHeight: '',
			editItems: [
				{
					type: 'textarea',
					value: ''
				}
			],
			locationCity: '北京',
			content: []
		};
	},
	props: {
		title: {
			type: String,
			default: ''
		},
		uploadUrl: {
			type: String,
			default: ''
		},
		paddingBottom: {
			//当键盘弹起的时候，输入框距离工具栏的距离
			type: Number,
			default: 0
		},
		placeText: {
			//文本框默认的文字
			type: String,
			default: '写点什么吧...'
		},
		currentDate: {
			type: String,
			default: '1900-01-01'
		},
		//默认的文本类型
		contentData: {
			type: Object
		}
	},
	onLoad() {},
	methods: {
		initTip() {
			this.showTip = false;
			this.current_editor_id = 0;
		},
		submit: function() {
			console.log(this);
			return this;
		},
		/**
		 * 键盘输入时触发事件
		 */
		inputIng: function(e) {
			var that = this;
			var value = e.detail.value;
			var index = e.currentTarget.dataset.index;
			that.$set(that.editItems[index], 'value', value);
		},
		/**
		 * 输入框行数变化
		 * 当输入框行数变化时，动态赋值给整个content的一个高度，避免输入框跑到工具栏下边去
		 */
		lineChange: function(e) {
			this.scroll_editor_id = ''; 
			setTimeout(() => {
				this.scroll_editor_id = 'scorll_end';
			}, 500);
		},
		insertImg: function() {
			var that = this;
			uni.chooseImage({
				count: 1,
				success: chooseImageRes => {
					const tempFilePaths = chooseImageRes.tempFilePaths;
					let result = that.editItems.push({
						type: 'img',
						value: tempFilePaths[0],
						id: 0,
						progress: 0
					});

					this.scroll_editor_id = '';
					this.$app.uploadFile({
						url: that.uploadUrl,
						filePath: tempFilePaths[0],
						name: 'file',
						formData: {},
						success: res => {
							console.log(res);
							console.log(res.code);
							if (res.code == 0) {
								that.$set(that.editItems[result - 1], 'value', res.data.url);
								that.$set(that.editItems[result - 1], 'id', res.data.id);
							}
							this.showTip = false;
							setTimeout(() => {
								that.editItems.push({
									type: 'textarea',
									value: ''
								});
							}, 500);
							this.scroll_editor_id = 'scorll_end';
							setTimeout(() => {
								this.current_editor_id = result;
							}, 500);
						}
					});
				}
			});
		},
		delImg: function(index) {
			var that = this;
			uni.showModal({
				title: '确定要删除该图片吗？',
				content: ' ',
				success: function(res) {
					if (res.confirm) {
						console.log('用户点击确定');
						that.editItems.splice(index, 1);
					}
				}
			});
			console.log(index);
		},
		insertVideo: function() {
			var that = this;
			uni.chooseVideo({
				success: chooseVideoRes => {
					const tempFilePath = chooseVideoRes.tempFilePath;
					console.log(chooseVideoRes);
					let result = that.editItems.push({
						type: 'video',
						value: tempFilePath,
						id: 0,
						progress: 0
					});
					this.$app.uploadFile({
						url: that.uploadUrl,
						filePath: tempFilePath,
						name: 'file',
						formData: {},
						success: res => {
							console.log(res);
							console.log(res.code);
							if (res.code == 0) {
								that.$set(that.editItems[result - 1], 'value', res.data.url);
								that.$set(that.editItems[result - 1], 'id', res.data.id);
							}
							this.showTip = false;
							setTimeout(() => {
								that.editItems.push({
									type: 'textarea',
									value: ''
								});
							}, 500);
							this.scroll_editor_id = 'scorll_end';
							setTimeout(() => {
								this.current_editor_id = result;
							}, 500);
						}
					});
				}
			});
		},
		delVideo: function(index) {
			var that = this;
			uni.showModal({
				title: '确定要删除该视频吗？',
				content: ' ',
				success: function(res) {
					if (res.confirm) {
						console.log('用户点击确定');
						that.editItems.splice(index, 1);
					}
				}
			});
			console.log(index);
		},
		chooseLocation: function() {
			uni.chooseLocation({
				success: function(res) {
					console.log('位置名称：' + res.name);
					console.log('详细地址：' + res.address);
					console.log('纬度：' + res.latitude);
					console.log('经度：' + res.longitude);
				}
			});
		}
	}
};
</script>

<style lang="scss" scoped>
.scroll-end {
	display: block;
	height: 20rpx;
	width: 100%;
	/* #ifdef MP */
	//margin-top: 50rpx;
	/* #endif */
}
.scorll-last {
	display: block;
	height: 50rpx;
	width: 100%;
}
.editor-tip {
	z-index: 9999;
	color: #999;
}
.editor {
	padding: 30rpx 10rpx;
	box-sizing: border-box;
	background: #fff;
	height: 600rpx;
	textarea {
		font-size: 28rpx;
		background: #fff;
		width: 100%;
		display: block;
	}
	image {
		margin: 10rpx 1%;
		width: 98%;
	}

	.img {
		position: relative;
		display: flex;
		justify-content: center;
		.del {
			position: absolute;
			top: 8rpx;
			right: 10rpx;
			image {
				height: 48rpx;
				width: 48rpx;
			}
		}
	}
	.video {
		.del {
			position: absolute;
			top: -30rpx;
			right: 6rpx;
		}
	}
}
uni-progress {
	width: 100%;
	display: none;
}
.edit-tools {
	border-bottom: 1rpx solid #eee;
	height: 90rpx;
	display: flex;
	align-items: center;
	justify-content: space-between;
	.title {
		flex-grow: 0;
		flex-shrink: 0;
		font-size: 28rpx;
		color: #999;
	}
	.operate {
		flex-grow: 1;
		flex-shrink: 1;
		display: flex;
		justify-content: flex-end;
		margin-right: 10rpx;
		.item {
			display: flex;
			align-items: center;
			margin-left: 30rpx;
			image {
				width: 35rpx;
				height: 35rpx;
			}
			text {
				font-size: 28rpx;
				color: #999;
				margin-left: 15rpx;
			}
		}
	}
}
</style>
