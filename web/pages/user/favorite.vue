<template>
	<view class="page">
		<view class="content">
			<scroller @init="initScroller" @down="refreshData" @up="getData" :up="optUp" :fixed="false"><articleList :list="list" /></scroller>
		</view>
		<pageLoading v-if="showPageLoading"></pageLoading>
	</view>
</template>

<script>
import scroller from '@/components/scroller/scroller.vue';
import articleList from '@/components/article/list.vue';
import pageLoading from '@/components/loading/pageLoading.vue';
import iconfont from '@/components/iconfont/iconfont.vue';
export default {
	components: {
		pageLoading,
		articleList,
		scroller,
		iconfont
	},
	data() {
		return {
			scroller: {},
			optUp: { auto: true, onScroll: true, empty: { tip: '暂无~' } },
			list: [],
			showPageLoading: true
		};
	},
	onShow() {
		this.$initPageTitle(); //初始化页面标题
		// /*来源是登录时更新*/
		// let source = uni.getStorageSync('source');
		// if (source == 'login') {
		// 	uni.removeStorageSync('source');
		// 	this.loadData();
		// }
		// if (this.scroller) {
		// 	this.getData();
		// }
	},
	methods: {
		/*初始化滚动*/
		initScroller(scroller) {
			this.scroller = scroller;
		},

		/*加载数据*/
		loadData() {
			this.list = [];
			this.scroller.resetUpScroll();
		},

		/*刷新数据*/
		refreshData() {
			uni.showLoading({
				title: '刷新中'
			});
			this.scroller.resetUpScroll();
		},

		/*获取数据*/
		getData() {
			this.$app.request({
				url: this.$api.article.favoriteList,
				data: {
					page_index: this.scroller.num,
					page_size: this.scroller.size
				},
				method: 'POST',
				dataType: 'json',
				success: res => {
					if (res.code == 0) {
						if (this.scroller.num == 1) {
							this.list = [];
						}
						this.list = this.list.concat(res.data.list);
						this.scroller.endByPage(res.data.list.length, res.data.page);
						this.showPageLoading = false;
					} else {
						this.scroller.endSuccess();
						this.$alert(res.msg);
					}
				},
				fail: res => {
					this.scroller.endErr();
				},
				complete: res => {
					uni.stopPullDownRefresh();
					uni.hideLoading();
				}
			});
		}
	}
};
</script>

<style scoped lang="scss">
page {
	height: 100%;
}
.page {
	display: flex;
	flex: 1;
	flex-direction: column;
	overflow: hidden;
	height: 100%;
}
.content {
	flex: 1;
	width: 100%;
	height: 100rpx;
}

.scroll {
	height: 100%;
}
.favorite-type {
	display: flex;
	flex-direction: row;
	align-items: center;
	justify-content: space-around;
	height: 86rpx;
	width: 100%;
	box-shadow: 3upx 3upx 8upx 0 #eee;
	background: #fff;
	.item {
		width: 350rpx;
		font-size: 34rpx;
		line-height: 80rpx;
		text-align: center;
	}

	.current {
		color: #ec9416;
		border-bottom: 2px solid #fcb90c;
	}
}
.article {
	margin-top: 2rpx;
	.item {
		padding: 40rpx 24rpx 0 24rpx;
		.info {
			display: flex;
			flex-direction: row;
			justify-content: space-between;
			padding-bottom: 4rpx;
			.text {
				flex-grow: 1;
				flex-shrink: 1;
				display: flex;
				flex-direction: column;
				margin-right: 40rpx;
				.title {
					flex-grow: 1;
					flex-shrink: 1;
					font-size: 34rpx;
					line-height: 1.5;
					display: -webkit-box;
					text-overflow: ellipsis;
					word-break: break-all;
					-webkit-line-clamp: 2;
					-webkit-box-orient: vertical;
					overflow: hidden;
				}
				.other {
					flex-grow: 1;
					flex-shrink: 1;
					display: flex;
					flex-direction: row;
					align-items: center;
					font-size: 28rpx;
					color: #999;
					.left {
						display: flex;
						flex-direction: row;
						flex-grow: 1;
						flex-shrink: 1;
						.source {
							margin-right: 16rpx;
						}
					}
					.right {
						display: flex;
						flex-direction: row;
						justify-content: center;
						align-items: center;
						flex-grow: 0;
						flex-shrink: 0;
						image {
							flex-grow: 1;
							flex-shrink: 1;
							width: 28rpx;
							height: 28rpx;
							margin-right: 12rpx;
						}
						text {
							flex-grow: 1;
							flex-shrink: 1;
							margin-top: -6rpx;
						}
					}
					.view {
						image {
							width: 40rpx;
							height: 40rpx;
							margin-right: 5rpx;
						}
					}
				}
			}
			.photo {
				image {
					height: 170rpx;
					width: 222rpx;
					border-radius: 10rpx;
				}
			}
		}
		.line {
			display: bock;
			width: 100%;
			height: 1rpx;
			margin-top: 22rpx;
			background: #e8e8e8;
		}
		&:last-child {
			.line {
				//display: none;
			}
			//padding-bottom: 30rpx;
		}
	}
}

.complaint {
	margin-top: 2rpx;
	.item {
		position: relative;
		padding: 40rpx 24rpx 0 24rpx;
		.info {
			display: flex;
			flex-direction: row;
			justify-content: space-between;
			align-items: center;
			padding-bottom: 4rpx;
			.text {
				flex-grow: 1;
				flex-shrink: 1;
				display: flex;
				flex-direction: column;
				margin-right: 10rpx;
				.title {
					flex-grow: 1;
					flex-shrink: 1;
					font-size: 32rpx;
					line-height: 1.5;
				}
				.car {
					margin-top: 20rpx;
					color: #666;
					display: flex;
					flex-direction: column;
					view {
						display: flex;
						align-items: center;
						/deep/ .icon {
							margin-right: 10rpx;
						}
					}
					.brand-series {
						/deep/ .icon {
							font-size: 30rpx;
						}
					}
					.model {
						/deep/ .icon {
							font-size: 30rpx;
						}
					}
				}
				.other {
					display: flex;
					flex-direction: row;
					align-items: center;
					font-size: 28rpx;
					color: #999;
					.left {
						display: flex;
						flex-direction: column;
						flex-grow: 1;
						flex-shrink: 1;
						margin-top: 10rpx;
					}
					.right {
						display: flex;
						flex-direction: row;
						justify-content: center;
						align-items: center;
						flex-grow: 0;
						flex-shrink: 0;
						margin-right: 10rpx;
						image {
							flex-grow: 1;
							flex-shrink: 1;
							width: 28rpx;
							height: 28rpx;
							margin-right: 12rpx;
						}
						text {
							flex-grow: 1;
							flex-shrink: 1;
							margin-top: -6rpx;
						}
					}
					.view {
						image {
							width: 40rpx;
							height: 40rpx;
						}
						margin-right: -212rpx;
					}
				}
			}
			.photo {
				image {
					height: 140rpx;
					width: 212rpx;
					border-radius: 10rpx;
				}
				margin-top: -40rpx;
			}
		}
		.line {
			width: 100%;
			height: 1rpx;
			margin-top: 22rpx;
			background: #e8e8e8;
		}
		&:last-child {
			.line {
				//display: none;
			}
			padding-bottom: 30rpx;
		}
	}
}
</style>
