<template>
	<view class="page">
		<view class="status-bar"></view>
		<view class="top">
			<!-- #ifdef MP -->
			<!-- 页面标题 -->
			<view class="page-title" :style="'height: ' + navBarHeight">
				<image src="../../static/images/logo.png"></image>
			</view>
			<!-- 搜索 -->
			<view class="search" :style="showNavFloat ? 'display:none' : ''">
				<navigator class="input" :url="'search?category_id=' + category_id" hover-class="none">
					<iconfont type="search"></iconfont>
					<text>输入关键字</text>
				</navigator>
			</view>
			<!-- #endif -->

			<!-- 搜索 -->
			<!-- #ifdef H5 -->
			<view class="search">
				<image class="logo" mode="aspectFit" src="../../static/images/logo.png"></image>
				<navigator class="input" :url="'search?category_id=' + category_id" hover-class="none">
					<iconfont type="search"></iconfont>
					<text>输入关键字</text>
				</navigator>
				<navigator class="user" url="/pages/user/index" hover-class="none">
					<iconfont type="user-01"></iconfont>
					<!-- <image v-if="current.user" :src="user.avatar_url"></image> -->
					<!-- <image src="/static/images/default_user_photo.jpg" @tap="login()"></image> -->
				</navigator>
			</view>
			<!-- #endif -->

			<!-- 导航 -->
			<view class="navbar" :class="showNavFloat ? 'floatbar' : ''">
				<view class="menu" v-if="category.length > 0">
					<view class="category">
						<scroll-view :scroll-x="true" :scroll-with-animation="true" :scroll-into-view="scroll_category_id" @scroll="navFloatShow()">
							<view class="item" v-for="(item, index) in category" :key="index" :class="category_id == item.id ? 'current' : ''"
							 :id="'category_id-' + index" :style="'width:' + (category.length <= 4 ? 100 / category.length + '%' : '')" @tap="categoryChange(item.id, index)">
								<view class="text">
									<text>{{ item.name }}</text>
									<!-- <image src="/static/images/bg_tab.png"></image> -->
								</view>
							</view>
						</scroll-view>
					</view>
					<view class="list" @tap="menuShow(!showMenu)">
						<iconfont type="menu-01"></iconfont>
					</view>
				</view>
			</view>
		</view>
		<view class="content">
			<view class="menu-block fade-in" v-show="showMenu">
				<view class="list">
					<text class="item" v-for="(item, index) in category" :key="index" :class="category_id == item.id ? 'current' : ''"
					 :id="'category_id-' + (index + 1)" @tap="categoryChange(item.id, index)">
						{{ item.name }}
					</text>
				</view>
			</view>
			<scroller @init="initScroller" @down="refreshData" @up="getData" :up="optUp" @scroll="navFloatShow(scroller)" :fixed="false">
				<swiper v-if="slider.length > 0" class="swiper" :indicator-dots="true" :autoplay="true" :circular="true">
					<swiper-item v-for="(item, index) in slider" :key="index">
						<navigator class="item" hover-class="none" :url="'/pages/article/detail?id=' + item.id">
							<image :lazy-load="true" :src="item.photo_url" mode="aspectFill"></image>
							<view class="title">
								<text>{{ item.title }}</text>
							</view>
						</navigator>
					</swiper-item>
				</swiper>
				<articleList :list="list" />
			</scroller>
		</view>
		<pageLoading v-if="showPageLoading"></pageLoading>
	</view>
</template>

<script>
	import scroller from '@/components/scroller/scroller.vue';
	import articleList from '@/components/article/list.vue';
	import pageLoading from '@/components/loading/pageLoading.vue';
	import iconfont from '@/components/iconfont/iconfont.vue';
	import util from '@/common/util.js';
	export default {
		components: {
			articleList,
			pageLoading,
			scroller,
			iconfont
		},
		data() {
			return {
				scroller: {},
				optUp: {
					auto: true,
					onScroll: true,
					page: {
						size: 20
					},
					empty: {
						tip: '暂无数据~'
					}
				},
				category_id: 1,
				category_index: 0,
				scroll_category_id: 'scroll_category_id_0',
				currentSliderIndex: 0,
				category: [],
				showMenu: false,
				slider: [],
				list: [],
				showNoData: false,
				showPageLoading: true,
				showNavFloat: false,
				navBarHeight: ''
			};
		},
		onShow() {
			this.$initPageTitle(); //初始化页面标题
			console.log('onShow')
			/*导航栏高度*/
			if (this.navBarHeight == '') {
				this.navBarHeight = this.$app.getNaviBarHeight();
			}
			uni.removeStorageSync('cancelLogin');
			/*来源是登录时更新*/
			let source = uni.getStorageSync('source');
			if (source == 'login') {
				uni.removeStorageSync('source');
				this.loadData();
			}
		},
		onShareAppMessage() {
			return {
				path: '/pages/article/index',
				success: function(e) {},
				title: '团节社成都'
			};
		},
		onLoad(e) {
			// #ifdef H5
			if (e.category_id > 0) {
				this.category_id = e.category_id;
			}
			if (e.category_index > 0) {
				this.category_index = e.category_index;
			}
			// #endif
			this.getCategory();
		},
		onPullDownRefresh() {
			uni.showLoading({
				title: '刷新中'
			});
			this.loadData();
		},
		methods: {
			/*初始化滚动*/
			initScroller(scroller) {
				this.scroller = scroller;
			},

			/*刷新数据*/
			refreshData() {
				uni.showLoading({
					title: '刷新中'
				});
				this.scroller.resetUpScroll();
			},

			/*加载数据*/
			loadData() {
				this.slider = [];
				this.list = [];
				this.currentSliderIndex = 0;
				this.scroller.resetUpScroll();
			},

			/*获取子类别数据*/
			getCategory() {
				this.$app.request({
					url: this.$api.article.category,
					method: 'POST',
					dataType: 'json',
					success: res => {
						if (res.code == 0) {
							this.category = res.data;
							if (this.category_index > -1) {
								let nextIndex = this.category_index - 1;
								nextIndex = nextIndex <= 0 ? 0 : nextIndex;
								this.scroll_category_id = `category_id-${nextIndex}`; //动画滚动,滚动至中心位置
							}
						} else {
							this.$alert(res.msg);
						}
					},
					complete: res => {}
				});
			},

			/*获取数据*/
			getData() {
				this.$app.request({
					url: this.$api.article.index,
					data: {
						category_id: this.category_id,
						page_index: this.scroller.num,
						page_size: this.scroller.size,
					},
					method: 'POST',
					dataType: 'json',
					success: res => {
						if (res.code == 0) {
							if (this.scroller.num == 1) {
								this.list = [];
							}
							if (this.slider.length == 0) {
								this.slider = res.data.slider;
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
			},

			/*切换导航*/
			categoryChange(category_id, index) {
				this.showMenu = false;
				this.category_index = index;
				this.category_id = category_id;
				var nextIndex = index - 1;
				nextIndex = nextIndex <= 0 ? 0 : nextIndex;
				this.scroll_category_id = `category_id-${nextIndex}`; //动画滚动,滚动至中心位置
				this.loadData();

				// #ifdef H5
				// uni.navigateTo({
				// 	url: '/pages/article/list?category_id=' + this.category_id + '&category_index=' + this.category_index
				// });
				// #endif
			},

			/*广告切换*/
			sliderChange: function(e) {
				this.currentSliderIndex = e.detail.current;
			},

			/*滚动时导航栏浮动*/
			navFloatShow(scroller) {
				if (scroller) {
					if (scroller.scrollTop > 60) {
						if (!this.showNavFloat) {
							this.showNavFloat = true;
						}
					} else {
						if (this.showNavFloat) {
							this.showNavFloat = false;
						}
					}
				}
			},
			/*菜单框展示*/
			menuShow(value) {
				this.showMenu = value;
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
		.page-title {
			display: flex;
			justify-content: center;
			align-items: center;
			height: 90rpx;
			text-align: center;
			border-bottom: 1rpx solid #efefef;
			z-index: 9999;
			line-height: 1;

			image {
				height: 50rpx;
				width: 280rpx;
				margin-left: -35rpx;
			}
		}

		/*搜索*/
		.search {
			padding: 24rpx 24rpx 0 24rpx;
			display: flex;
			justify-content: space-between;
			align-items: center;

			.logo {
				height: 60rpx;
				width: 474rpx;
				margin-right: 30rpx;
			}

			.input {
				margin-left: 0 !important;
				display: flex;
				align-items: center;
				height: 70rpx;
				width: 100%;
				background: #f5f5f5;
				border-radius: 34rpx 34rpx 0 34rpx;

				/deep/ .icon {
					margin-left: 28rpx;
					color: #aaaaaa;
					font-size: 32rpx;
					line-height: 1;
				}

				text {
					margin-left: 14rpx;
					font-size: 30rpx;
					color: #aaaaaa;
					line-height: 1;
				}
			}

			.user {
				display: flex;
				align-items: center;

				/deep/ .icon {
					margin-left: 24rpx;
					color: #aaaaaa;
					font-size: 38rpx;
					margin-top: 4rpx;
				}

				image {
					width: 50rpx;
					height: 50rpx;
					border-radius: 50%;
					margin-left: 25rpx;
				}
			}
		}

		/* 顶部navbar */
		.navbar {

			/*分类*/
			.menu {
				position: relative;
				height: 80rpx;
				white-space: nowrap;
				padding: 15rpx 0 6rpx;
				z-index: 10;
				display: flex;
				align-items: center;
				justify-content: space-between;

				/*分类*/
				.category {
					width: 650rpx;
					margin-left: 30rpx;
					white-space: nowrap;
					position: relative;

					scroll-view {
						width: auto;

						.item {
							position: relative;
							display: inline-block;
							margin: 0 20rpx 0;
							height: 80rpx;
							text-align: left;
							padding-top: 7rpx;

							//line-height: 80rpx;
							&:first-child {
								margin-left: 10rpx;
							}

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
									font-size: 36rpx;
									font-weight: bold;
									color: #555;
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
								text {
									font-size: 40rpx;
									font-weight: bold;
									color: #262626;
								}

								image {
									display: block;
								}

								border-bottom: 6rpx solid #ffd100;
							}
						}
					}
				}

				.list {
					width: 70rpx;
					display: flex;
					align-items: center;
					justify-content: center;

					//box-shadow: -4rpx 0 0 #e9ebee;
					//box-shadow: -2px 0 0 #262626;
					/deep/ .icon {
						font-size: 36rpx;
						margin-top: -6rpx;
					}
				}
			}
		}
	}

	.menu-block {
		position: absolute;
		top: 0;
		left: 0;
		background: #fff;
		border-bottom: 1rpx solid #f5f5f5;
		padding: 20rpx 0 50rpx;
		z-index: 100;
		width: 100%;

		//box-shadow: 0 15rpx 10rpx -15rpx #e9ebee;
		.list {
			padding-left: 10rpx;

			text {
				background: #f5f7fa;
				border-radius: 8rpx;
				font-size: 32rpx;
				margin: 12rpx 20rpx;
				display: inline-block;
				height: 72rpx;
				width: 144rpx;
				line-height: 72rpx;
				text-align: center;
			}

			.current {
				color: #fff;
				background: #ffd100;
				//font-size: 32rpx;
			}
		}
	}

	.scroll {
		height: 100%;
	}

	/*幻灯片广告 */
	.swiper {
		height: 350rpx;
		margin: 14rpx 24rpx 0;

		image {
			height: 350rpx;
			width: 100%;
			border-radius: 15rpx;
			//box-shadow: 0 0 12px #dddee1;
		}

		.current {
			width: 97%;
			height: 350rpx;
			margin: 1.5% 1.5%;
			transition: all 0.2s ease-in 0s;
		}

		.item {
			position: relative;

			.title {
				position: absolute;
				left: 0;
				bottom: 0;
				display: flex;
				justify-content: center;
				width: 100%;

				text {
					margin: 7% 6%;
					display: flex;
					align-items: center;
					//background-image: linear-gradient(to right, rgba(0, 0, 0, 0.3), rgba(0, 0, 0, 0.3));
					//background-image: linear-gradient(90deg,rgba(255, 181, 19,.8),rgba(255, 181, 19,.2));
					color: #ffffff;
					display: -webkit-box;
					text-overflow: ellipsis;
					word-break: break-all;
					-webkit-line-clamp: 2;
					-webkit-box-orient: vertical;
					overflow: hidden;
					font-size: 32rpx;
					font-weight: bold;
					padding: 7rpx 10rpx;
					max-width: 610rpx;
					line-height: 1.3;
					border-radius: 5rpx;
				}
			}
		}
	}

	/*浮动navbar*/
	.floatbar {
		//border-bottom: 1rpx solid #e8e8e8;
		box-shadow: 0px 2px 2px -2px #e8e8e8;
		padding-bottom: 12rpx;
	}

	/deep/ .no-data {
		margin-top: 200rpx;
	}
</style>
