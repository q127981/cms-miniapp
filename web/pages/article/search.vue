<template>
	<view class="page">
		<view class="status-bar"></view>
		<view class="top">
			<!-- 页面标题 -->
			<!-- #ifdef MP -->
			<view class="page-title" :style="'height: ' + navBarHeight">
				<navigator class="back" open-type="navigateBack"><iconfont type="back"></iconfont></navigator>
				<view class="logo"><image src="../../static/images/logo.png"></image></view>
			</view>
			<!-- #endif -->
			<!--搜索-->
			<search :name="searchName" :keyword.sync="keyword" :history.sync="history" :showHistory.sync="showHistory" @confirm="search" @focus="initSearch"></search>
			<!-- 导航 -->
			<scroll-view
				v-if="!showHistory"
				class="navbar"
				:class="showNavFloat ? 'floatbar' : ''"
				:scroll-x="true"
				:scroll-with-animation="true"
				:scroll-into-view="scroll_category_id"
			>
				<view
					class="item"
					v-for="(item, index) in nav"
					:key="index"
					:class="category_id == item.id ? 'current' : ''"
					:id="'category_id-' + index"
					:style="'width:' + (nav.length <= 4 ? 100 / nav.length + '%' : '')"
					@tap="navChange(item.id)"
				>
					<view class="text">
						<text>{{ item.name }}</text>
						<image src="/static/images/bg_tab.png"></image>
					</view>
				</view>
			</scroll-view>
		</view>
		<view class="content" v-if="!showHistory">
			<scroll-view class="scroll" scroll-y @scrolltolower="loadMore">
				<articleList :list="list" />
				<loading :status="hasMoreData"></loading>
				<noData v-if="showNoData" text="暂无相关~"></noData>
			</scroll-view>
		</view>
		<pageLoading v-if="showPageLoading"></pageLoading>
	</view>
</template>

<script>
import search from '@/components/search/search.vue';
import articleList from '@/components/article/list.vue';
import loading from '@/components/loading/loading.vue';
import pageLoading from '@/components/loading/pageLoading.vue';
import noData from '@/components/noData/noData.vue';
import iconfont from '@/components/iconfont/iconfont.vue';
export default {
	components: {
		search,
		articleList,
		pageLoading,
		loading,
		noData,
		iconfont
	},
	data() {
		return {
			searchName: 'article-search',
			keyword: '',
			history: [],
			showHistory: true,
			category_id: '0',
			scroll_category_id: 'scroll_category_id_0',
			page_index: 1,
			page_size: 10,
			nav: [],
			slider: [],
			list: [],
			showNoData: false,
			showPageLoading: false,
			hasMoreData: true,
			showNavFloat: false
		};
	},
	onShow() {
		this.$initPageTitle(); //初始化页面标题
	},
	onLoad(e) {
		if (e.category_id > 0) {
			this.category_id = e.category_id;
		}
		let history = uni.getStorageSync(this.searchName);
		if (Array.isArray(history)) {
			this.history = history;
		}
	},
	onReady() {},
	methods: {
		/*搜索*/
		initSearch() {
			this.showHistory = true;
		},
		search(keyword) {
			if (keyword != '') {
				this.keyword = keyword;
			}
			this.showHistory = false;
			if (!this.history.includes(this.keyword)) {
				this.history.push(this.keyword);
				uni.setStorageSync(this.searchName, this.history);
			}
			this.nav = [];
			this.tabs = [];
			this.loadData();
		},

		/*加载数据*/
		loadData() {
			this.hasMoreData = true;
			this.page_index = 1;
			this.showNoData = false;
			this.slider = [];
			this.list = [];
			this.getData();
		},

		/*获取数据*/
		getData() {
			this.$app.request({
				url: this.$api.article.index,
				data: {
					keyword: this.keyword,
					category_id: this.category_id,
					page_index: this.page_index,
					page_size: this.page_size
				},
				method: 'POST',
				dataType: 'json',
				success: res => {
					if (res.code == 0) {
						if (this.page_index == 1) {
							this.list = [];
						}
						if (res.data.page > this.page_index) {
							this.hasMoreData = true;
						} else {
							this.hasMoreData = false;
						}
						var tempList=this.list.concat(res.data.list);
						
						this.list = tempList;
						if (this.page_index == 1) {
							if (this.list.length > 0) {
								this.showNoData = false;
							} else {
								this.showNoData = true;
							}
						}
						this.showPageLoading = false;
						this.page_index += 1;
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

		/*切换导航*/
		navChange(category_id) {
			this.category_id = category_id;
			this.loadData();
		},

		/*加载更多*/
		loadMore() {
			if (this.hasMoreData) {
				this.getData();
			}
		},

		/*滚动时导航栏浮动*/
		navFloatShow(e) {
			if (e.detail.scrollTop > 50) {
				if (!this.showNavFloat) {
					this.showNavFloat = true;
				}
			} else {
				if (this.showNavFloat) {
					this.showNavFloat = false;
				}
			}
		},

		/*隐藏导航浮动*/
		navFloatHide() {
			this.showNavFloat = false;
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

/*头部*/
.top {
	/*页面标题*/
	padding-bottom: 20rpx;
	.page-title {
		padding: 16rpx 0 6rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		height: 90rpx;
		text-align: center;
		border-bottom: 1rpx solid #efefef;
		z-index: 9999;
		.back {
			flex-grow: 0;
			flex-shrink: 0;
			line-height: normal;
			padding-left: 30rpx;
			/deep/.icon {
				color: #c1c4c9;
				font-size: 36rpx;
			}
		}
		.logo {
			flex-grow: 1;
			flex-shrink: 1;
			line-height: normal;
			image {
				height: 50rpx;
				width: 280rpx;
				margin-left: -110rpx;
			}
		}
	}

	/* 顶部navbar */
	.navbar {
		position: relative;
		height: 80rpx;
		white-space: nowrap;
		z-index: 10;
		border-bottom: 1px solid #e8e8e8;
		.item {
			position: relative;
			display: inline-block;
			width: 150rpx;
			height: 80rpx;
			text-align: center;
			padding-top: 7rpx;
			line-height: 80rpx;
			&:after {
				content: '';
				width: 0;
				height: 0;
				position: absolute;
				left: 50%;
				bottom: 0;
				transform: translateX(-50%);
				transition: 0.3s;
			}
			.text {
				position: relative;
				width: auto;
				height: auto;
				line-height: auto;
				display: inline-block;
				text {
					font-size: 34rpx;
					color: #262626;
				}
				image {
					position: absolute;
					top: 16rpx;
					right: -14rpx;
					width: 50rpx;
					height: 50rpx;
					display: none;
				}
			}
		}
		.current {
			&:after {
				width: 50%;
			}
			.text {
				font-weight: bold;
				image {
					display: block;
				}
			}
		}
	}

	/*浮动navbar*/
	.floatbar {
		box-shadow: 0 8upx 8upx rgba(0, 0, 0, 0.06);
		border-bottom: 0;
	}
}

.scroll {
	height: 100%;
}

/*幻灯片广告 */
.swiper {
	height: 320rpx;
	image {
		width: 97%;
		margin: 3.5% 1.5%;
		height: 260rpx;
		border-radius: 24rpx;
		transition: all 0.2s ease-in 0s;
	}
	.current {
		width: 97%;
		height: 300rpx;
		margin: 1.5% 1.5%;
		transition: all 0.2s ease-in 0s;
	}
	.title {
		position: absolute;
		bottom: 0;
		left: 0;
		width: 100%;
		background: rgba(0, 0, 0, 0.5);
		color: #fff;
		padding: 20rpx;
		white-space: nowrap;
		text-overflow: ellipsis;
		overflow: hidden;
		font-size: 11pt;
		display: none;
	}
}

/deep/ .navbar {
	display: none;
}
</style>
