<template>
	<view class="container">
		<view class="content">
			<view class="detail">
				<view class="title">{{ info.title }}</view>
				<view class="info">
					<view class="source-date">
						<text class="source" v-if="info.source_url" @tap="copyText(info.source_url)">{{ info.source }}</text>
						<text class="source" v-else>{{ info.source }}</text>
						<text class="date hidden">{{ info.create_time }}</text>
					</view>
					<view class="read">阅读 {{ info.read }}</view>
				</view>
				<view class="desc"><parser :html="info.content"></parser></view>
				<view class="declaration" v-if="info.source_url">
					本文转载至{{ info.source }}，
					<text @tap="copyText(info.source_url)">点此可查看原文链接。</text>
					如有侵权，请联系我们，我们将在最短的时间内处理。
				</view>
			</view>
			<view class="comment">
				<view class="title">
					<text>全部评论</text>
					<text class="count" v-if="info.comment.count > 0">({{ info.comment.count }})</text>
				</view>
				<view class="item" v-for="(item, index) in info.comment.list" :key="index">
					<view class="avatar"><image :src="item.avatar_url"></image></view>
					<view class="comment-info">
						<view class="nickname-like">
							<view class="nickname">{{ item.nickname }}</view>
							<view class="like">
								<image @tap="commentLike(item.id, index)" v-show="!item.is_like" src="/static/images/icon_like_01.png"></image>
								<image @tap="commentLike(item.id, index)" v-show="item.is_like" src="/static/images/icon_like_01_hover.png"></image>
								<view class="like-count">{{ item.like_count }}</view>
							</view>
						</view>
						<navigator class="comment-desc" :url="'commentDetail?id=' + item.id" hover-class="none">{{ item.content }}</navigator>
						<view class="date-reply">
							<navigator class="date" :url="'commentDetail?id=' + item.id" hover-class="none">{{ item.create_time }}</navigator>
							<navigator class="reply" :url="'commentDetail?operate=reply' + '&id=' + item.id">回复TA</navigator>
						</view>
						<navigator class="reply-count" v-if="item.reply_count > 0" :url="'commentDetail?id=' + item.id" hover-class="none">
							全部{{ item.reply_count }}条回复 >
						</navigator>
					</view>
				</view>
				<loading v-if="hasMoreData"></loading>
				<view class="no-comment" v-if="info.comment.count == 0">暂无评论</view>
			</view>
			<view class="float-empty"></view>
			<view class="comment-bar" v-show="!showAddComment && showCommenBar">
				<view class="input" @tap="initAddComment(true)"><text>发表评论</text></view>
				<view class="operate">
					<navigator :url="'comment?article_id=' + id" class="info" hover-class="none">
						<image src="/static/images/icon_comment_02.png"></image>
						<text class="count" v-if="info.comment.count > 0">{{ info.comment.count }}</text>
					</navigator>
					<view class="like">
						<image @tap="like()" v-show="!info.is_like" src="/static/images/icon_like_02.png"></image>
						<image @tap="like()" v-show="info.is_like" src="/static/images/icon_like_02_hover.png"></image>
					</view>
					<view class="favorite">
						<image @tap="favorite()" v-show="!info.is_favorite" src="/static/images/icon_favorite.png"></image>
						<image @tap="favorite()" v-show="info.is_favorite" src="/static/images/icon_favorite_hover.png"></image>
					</view>
					<button open-type="share" class="share" @tap="share"><image src="/static/images/icon_share.png"></image></button>
				</view>
			</view>
			<!-- #ifdef MP -->
			<view class="bg-masker" v-if="showAddComment" @tap="initAddComment(false)"></view>
			<view class="add-comment" v-if="showAddComment">
				<view class="info">
					<view class="input">
						<textarea
							:show-confirm-bar="false"
							:cursor-spacing="20"
							ref="commentContent"
							:maxlength="200"
							:auto-height="true"
							:fixed="true"
							:focus="showAddComment"
							v-model.trim="commentContent"
							placeholder-class="input-bg"
							:placeholder="replyCommentId > 0 ? '回复 ' + replyNickname + '：' : '发表评论'"
						/>
					</view>
					<view class="add">
						<button class="mini-btn" type="primary" size="mini" @click="addCommentOrReply()">{{ replyCommentId > 0 ? '回复' : '发表' }}</button>
					</view>
				</view>
			</view>
			<!-- #endif -->
			<!-- #ifndef MP -->
			<uni-popup :show="showAddComment" @close="initAddComment(false)" type="bottom">
				<view class="add-comment">
					<view class="info">
						<view class="input">
							<textarea
								:cursor-spacing="20"
								:show-confirm-bar="false"
								:auto-height="true"
								:fixed="true"
								:maxlength="200"
								:focus="addCommentFocus"
								v-model.trim="commentContent"
								placeholder-class="input-bg"
								:placeholder="replyCommentId > 0 ? '回复 ' + replyNickname + '：' : '发表评论'"
							/>
						</view>
						<view class="add">
							<button class="mini-btn" type="primary" size="mini" @click="addCommentOrReply()">{{ replyCommentId > 0 ? '回复' : '发表' }}</button>
						</view>
					</view>
				</view>
			</uni-popup>
			<!-- #endif -->
			<uni-popup :show="showH5Share" type="bottom">
				<view class="h5-share">
					<view class="title">分享到</view>
					<view class="share-icon">
						<view @tap="showBrowserShareTips()">
							<image class="wechat" src="/static/images/icon_wechat.png"></image>
							<text>微信</text>
						</view>
						<view @tap="showBrowserShareTips()">
							<image src="/static/images/icon_qq.png"></image>
							<text>QQ</text>
						</view>
						<view @tap="openLink('https://service.weibo.com/share/share.php?url=' + currentUrl + '&title=' + info.title + '&pic=' + info.photo_url)">
							<image src="/static/images/icon_weibo.png"></image>
							<text>新浪微博</text>
						</view>
						<view
							@tap="openLink('https://sns.qzone.qq.com/cgi-bin/qzshare/cgi_qzshare_onekey??url=' + currentUrl + '&title=' + info.title + '&pics=' + info.photo_url)"
						>
							<image src="/static/images/icon_qqkj.png"></image>
							<text>QQ空间</text>
						</view>
					</view>
					<view class="share-btn" @tap="closeH5Share">取消</view>
				</view>
			</uni-popup>
			<view @tap="closeShareTip()" class="share-maske" v-show="showShareTip">
				<view class="share-tip">
					<image class="share-tip-info" mode="aspectFit" src="/static/images/share_tip_info.png"></image>
					<image @tap="closeShareTip()" class="share-tip-btn" mode="aspectFit" src="/static/images/share_tip_btn.png"></image>
				</view>
			</view>
			<view @tap="closeBrowserShareTip()" class="share-maske" v-show="showBrowserShareTip">
				<view class="share-tip share-browser-tip">
					<image class="share-tip-info" mode="aspectFit" src="/static/images/share_tip_browser.png"></image>
					<image @tap="closeBrowserShareTip()" class="share-tip-btn" mode="aspectFit" src="/static/images/share_tip_btn.png"></image>
				</view>
			</view>
		</view>
		<pageLoading v-if="showPageLoading"></pageLoading>
	</view>
</template>

<script>
import parser from '@/components/parser/parser.vue';
import pageLoading from '@/components/loading/pageLoading.vue';
import iconfont from '@/components/iconfont/iconfont.vue';
import loading from '@/components/loading/loading.vue';
import uniPopup from '@/components/uni-popup/uni-popup.vue';
import util from '@/common/util.js';
import wechatMP from '@/common/sdk/wechatMP.js';
export default {
	components: {
		parser,
		pageLoading,
		loading,
		iconfont,
		uniPopup
	},
	data() {
		return {
			id: 0,
			info: { comment: { count: 0, list: [] } },
			showAddComment: false,
			showCommenBar: true,
			addCommentFocus: false,
			commentContent: '',
			replyCommentId: 0,
			replyNickname: '',
			page_index: 1,
			page_size: 10,
			hasMoreData: false,
			showPageLoading: true,
			currentUrl: '',
			shareUrl: '',
			showShareTip: false,
			showH5Share: false,
			showBrowserShareTip: false
		};
	},
	onShow(e) {
		this.$initPageTitle(); //初始化页面标题
		if (this.page_index > 1) {
			this.page_index = 1;
			this.getData();
		}

		/*来源是登录时更新*/
		let source = uni.getStorageSync('source');
		if (source == 'login') {
			uni.removeStorageSync('source');
			this.loadData();
		}

		/*当前页面*/
		if (!uni.getStorageSync('source')) {
			uni.setStorageSync('source', 'article/detail');
		}
	},
	onLoad(e) {
		if (e.id > 0) {
			this.id = e.id;
		}
		// #ifdef H5
		this.currentUrl = encodeURIComponent(window.location.href);
		// #endif
		this.getData();
	},
	onPullDownRefresh() {
		uni.showLoading({
			title: '刷新中'
		});
		this.page_index = 1;
		this.getData();
	},
	onReachBottom(e) {
		if (this.hasMoreData) {
			this.getComment();
		}
	},
	/*小程序分享*/
	onShareAppMessage() {
		return {
			path: '/pages/article/detail?id=' + this.info.id,
			success: function(e) {},
			title: this.info.title,
			imageUrl: this.info.photo_url
		};
	},
	methods: {
		/*加载数据*/
		loadData() {
			this.getData();
		},

		/*获取文章详情*/
		getData() {
			this.$app.request({
				url: this.$api.article.detail,
				data: {
					id: this.id,
					page_size: this.page_size
				},
				method: 'POST',
				dataType: 'json',
				success: res => {
					console.log('res');
					if (res.code == 0) {
						this.info = res.data;
						uni.setNavigationBarTitle({
							title: this.info.title
						});
						this.$initPageTitle();
						this.showPageLoading = false;
						if (res.data.comment.page > this.page_index) {
							this.hasMoreData = true;
						} else {
							this.hasMoreData = false;
						}
						this.page_index += 1;
						this.initMPShare(); //公众号分享
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

		/*点赞*/
		like() {
			this.$app.request({
				url: this.$api.article.like,
				data: {
					article_id: this.id
				},
				method: 'POST',
				dataType: 'json',
				success: res => {
					console.log('res');
					if (res.code == 0) {
						this.info.is_like = !this.info.is_like;
						let msg = this.info.is_like ? '点赞成功' : '已取消点赞';
						this.$alert(msg, 'success');
					} else {
						this.$alert(res.msg, 'warning');
					}
				}
			});
		},

		/*评论点赞*/
		commentLike(comment_id, index) {
			this.$app.request({
				url: this.$api.article.commentLike,
				data: {
					comment_id: comment_id
				},
				method: 'POST',
				dataType: 'json',
				success: res => {
					if (res.code == 0) {
						if (this.info.comment.list[index].is_like) {
							this.info.comment.list[index].is_like = false;
							this.info.comment.list[index].like_count -= 1;
						} else {
							this.info.comment.list[index].is_like = true;
							this.info.comment.list[index].like_count += 1;
						}
						let msg = this.info.comment.list[index].is_like ? '点赞成功' : '已取消点赞';
						this.$alert(msg);
					} else {
						this.$alert(res.msg);
					}
				}
			});
		},

		/*收藏*/
		favorite() {
			this.$app.request({
				url: this.$api.article.favorite,
				data: {
					article_id: this.id
				},
				method: 'POST',
				dataType: 'json',
				success: res => {
					console.log('res');
					if (res.code == 0) {
						this.info.is_favorite = !this.info.is_favorite;
						let msg = this.info.is_favorite ? '收藏成功' : '已取消收藏';
						this.$alert(msg, 'success');
					} else {
						this.$alert(res.msg, 'warning');
					}
				}
			});
		},

		/*初始化添加评论*/
		initAddComment(status) {
			if (status) {
				this.$app.initLogin();
			}
			this.showAddComment = status;
			this.commentContent = '';
			if (this.replyCommentId > 0) {
				this.replyCommentId = 0;
				this.replyNickname = '';
			}
			setTimeout(() => {
				this.addCommentFocus = status;
			}, 200);
		},

		/*添加评论或回复*/
		addCommentOrReply() {
			if (this.commentContent == '') {
				this.$alert('评论内容不能为空');
				return;
			}
			if (this.replyCommentId > 0) {
				this.addReply();
			} else {
				this.addComment();
			}
		},

		/*添加评论*/
		addComment() {
			this.$app.request({
				url: this.$api.article.addComment,
				data: {
					article_id: this.id,
					content: this.commentContent,
					page_size: this.page_size
				},
				method: 'POST',
				dataType: 'json',
				success: res => {
					if (res.code == 0) {
						this.info.comment = res.data;
						this.showAddComment = false;
						this.commentContent = '';
						this.$alert('评论成功');
					} else {
						this.$alert(res.msg);
					}
				}
			});
		},

		/*初始化回复*/
		initAddReply(comment_id, nickname) {
			this.initAddComment(true);
			this.replyCommentId = comment_id;
			this.replyNickname = nickname;
		},

		/*添加回复*/
		addReply() {
			if (this.commentContent == '') {
				this.$alert('回复内容不能为空');
				return;
			}
			this.$app.request({
				url: this.$api.article.addReply,
				data: {
					comment_id: this.replyCommentId,
					content: this.commentContent,
					page_size: this.page_size
				},
				method: 'POST',
				dataType: 'json',
				success: res => {
					if (res.code == 0) {
						this.info.comment = res.data;
						this.showAddComment = false;
						this.replyCommentId = 0;
						this.replyNickname = '';
						this.commentContent = '';
						this.$alert('评论成功');
					} else {
						this.$alert(res.msg);
					}
				}
			});
		},

		/*获取评论*/
		getComment() {
			this.$app.request({
				url: this.$api.article.comment,
				data: {
					article_id: this.id,
					page_index: this.page_index,
					page_size: this.page_size
				},
				method: 'POST',
				dataType: 'json',
				success: res => {
					if (res.code == 0) {
						console.log(this.info.comment);
						this.info.comment.list = this.info.comment.list.concat(res.data.list);
						this.info.comment.count = res.data.count;
						if (res.data.page > this.page_index) {
							this.hasMoreData = true;
						} else {
							this.hasMoreData = false;
						}
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

		/*打开外部链接*/
		openLink(url) {
			console.log(url);
			window.location.href = url;
		},

		/*关闭H5分享*/
		closeH5Share() {
			this.showH5Share = false;
		},

		/*关闭微信分享提示*/
		closeShareTip() {
			this.showShareTip = false;
		},
		/*显示浏览器分享提示*/
		showBrowserShareTips() {
			this.showH5Share = false;
			this.showBrowserShareTip = true;
		},
		/*关闭浏览器分享提示*/
		closeBrowserShareTip() {
			this.showBrowserShareTip = false;
		},

		/*分享*/
		share() {
			// #ifdef H5
			if (util.isWechatOrQQ()) {
				this.showShareTip = true;
			} else {
				this.showH5Share = true;
			}
			// #endif

			console.log('share');

			// #ifdef MP
			uni.showShareMenu();
			// #endif
		},

		/*初始化公众号分享*/
		initMPShare() {
			// #ifdef H5
			let domain = location.href.split('/pages/')[0];
			/*分享链接*/
			let link = domain + '/pages/article/detail?id=' + this.info.id;
			if (this.$app.isWechat()) {
				/*初始化分享*/
				wechatMP.initShare(this, {
					title: this.info.title,
					desc: '',
					link: link,
					imgUrl: this.info.photo_url
				});
			} else {
				this.shareUrl = link;
			}
			// #endif
		},

		/*跳转至评论详情*/
		redirectCommentDetail(id, replyCount) {
			if (replyCount > 0) {
				uni.navigateTo({
					url: 'commentDetail?id=' + id
				});
			}
		},

		/*复制*/
		copyText(text) {
			uni.setClipboardData({
				data: text,
				success: res => {
					this.$alert('原文链接已复制', 'success');
				}
			});
		}
	}
};
</script>

<style scoped lang="scss">
.detail {
	padding: 30rpx 30rpx;
	background: #fff;
	.title {
		font-size: 36rpx;
		color: #262626;
		line-height: 50rpx;
		font-weight: bold;
	}
	.info {
		margin-top: 26rpx;
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		align-items: center;
		.source-date {
			.source {
				color: #666;
			}
			.date {
				color: #999;
				margin-left: 24rpx;
			}
		}
		.read {
			color: #999;
		}
	}
	.desc {
		margin-top: 50rpx;
		overflow: hidden;
		color: #262626;
		.wxParse {
			color: #262626;
		}
	}
}
.comment {
	padding: 20rpx 32rpx 0;
	background: #fff;
	.title {
		display: flex;
		align-items: center;
		font-size: 36rpx;
		color: #262626;
		font-weight: bold;
		border-bottom: 1rpx solid #eee;
		text {
			padding-bottom: 20rpx;
			line-height: normal;
		}
		.count {
			padding-bottom: 20rpx;
			font-size: 30rpx;
			color: #999;
			margin-left: 10rpx;
			line-height: normal;
		}
	}
	.item {
		display: flex;
		justify-content: space-between;
		padding: 30rpx 0 20rpx;
		border-bottom: 1rpx solid #eee;
		&:last-child {
			border-bottom: 0;
		}
		.avatar {
			flex-grow: 0;
			flex-shrink: 0;
			margin-right: 20rpx;
			image {
				width: 45rpx;
				height: 45rpx;
				border-radius: 50%;
			}
		}
		.comment-info {
			flex-grow: 1;
			flex-shrink: 1;
			.nickname-like {
				display: flex;
				justify-content: space-between;
				.nickname {
					font-size: 32rpx;
					color: #444;
					font-weight: bold;
				}
				.like {
					display: flex;
					align-items: center;
					image {
						width: 32rpx;
						height: 32rpx;
						margin-right: 14rpx;
					}
					.like-count {
						color: #999;
						font-size: 30rpx;
					}
				}
			}
			.comment-desc {
				color: #444;
				font-size: 32rpx;
				line-height: 1.6;
				margin-top: 15rpx;
			}
			.reply-count {
				background: #f2f2f2;
				padding: 8rpx 20rpx;
				font-size: 30rpx;
				color: #999;
				margin: 20rpx 0 24rpx;
			}
			.date-reply {
				display: flex;
				justify-content: space-between;
				align-items: center;
				.date {
					display: flex;
					align-items: center;
					color: #b4b4b4;
					font-size: 28rpx;
					margin-right: 20rpx;
					margin-top: 4rpx;
				}
				.reply {
					flex-grow: 0;
					flex-shrink: 0;
					font-size: 28rpx;
					color: #999;
				}
			}
		}
	}
	.no-comment {
		padding: 40rpx 0;
		color: #999;
		font-size: 30rpx;
	}
}
.comment-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	width: 100%;
	height: 120rpx;
	background-color: #ffffff;
	box-shadow: 0 1px 1px 1px rgba(0, 0, 0, 0.3);
	display: flex;
	flex-direction: row;
	.input {
		flex-grow: 0;
		flex-shrink: 0;
		margin: 20rpx 10rpx 20rpx 20rpx;
		display: flex;
		align-items: center;
		background: #f2f2f2;
		width: 420rpx;
		border-radius: 40rpx 40rpx 0 40rpx;
		text {
			font-size: 32rpx;
			color: #aaa;
			margin-left: 38rpx;
		}
	}
	.operate {
		width: 100%;
		flex-grow: 1;
		flex-shrink: 1;
		display: flex;
		justify-content: space-around;
		align-items: center;
		margin-right: 10rpx;
		view,
		navigator {
			display: flex;
			justify-content: space-around;
			align-items: center;
			width: 25%;
			image {
				width: 45rpx;
				height: 45rpx;
			}
		}
		.info,
		.like {
			position: relative;
			.count {
				position: absolute;
				top: -15rpx;
				right: -4rpx;
				background-image: linear-gradient(140deg, #bee7e9 9%, #ffd100 75%);
				display: flex;
				align-items: center;
				border-radius: 50rpx;
				padding: 0 10rpx;
				font-size: 22rpx;
				color: #fff;
				height: 30rpx;
				line-height: 30rpx;
			}
		}
		.share {
			display: flex;
			justify-content: space-around;
			padding: 0;
			background: transparent;
			image {
				width: 45rpx;
				height: 45rpx;
				border: 0;
			}
			&:after {
				display: none;
			}
		}
	}
}
.float-empty {
	height: 120rpx;
	width: 100%;
	display: block;
	background: #fff;
}
.add-comment {
	position: fixed;
	bottom: 0;
	left: 0;
	width: 100%;
	background-color: #ffffff;
	z-index: 9999;
	.info {
		display: flex;
		flex-direction: row;
		padding: 20rpx 20rpx 20rpx 20rpx;
		.input {
			flex-grow: 1;
			flex-shrink: 1;
			display: flex;
			align-items: center;
			background: #f2f2f2;
			border-radius: 40rpx 40rpx 0 40rpx;
			padding: 20rpx 32rpx 20rpx;
			line-height: normal;
			textarea {
				//padding: 10rpx 0;
				width: 100%;
				font-size: 32rpx;
				line-height: 42rpx;
				background: #f2f2f2;
			}
		}
		.add {
			flex-grow: 0;
			flex-shrink: 0;
			width: 150rpx;
			display: flex;
			align-items: center;
			margin-left: 10rpx;
		}
		.cancel {
			position: absolute;
			right: 10rpx;
			top: 2rpx;
			/deep/.icon {
				font-size: 40rpx;
				color: #ccc;
			}
		}
	}
}
.h5-share {
	position: fixed;
	bottom: 0;
	left: 0;
	width: 100%;
	background-color: #ffffff;
	border-top: 1rpx solid #ddd;
	padding-top: 20rpx;
	padding-bottom: 20rpx;
	.title {
		font-size: 30rpx;
		text-align: center;
		height: 50rpx;
	}
	.share-icon {
		display: flex;
		flex-direction: row;
		justify-content: space-around;
		view {
			flex-grow: 0;
			flex-shrink: 0;
			text-align: center;
			image {
				margin-top: 35rpx;
				width: 80rpx;
				height: 80rpx;
			}
			text {
				display: block;
			}
		}
	}
	.share-btn {
		margin: 30rpx 30rpx 15rpx;
		border: 1rpx solid #ffd100;
		background: #ffd100;
		border-radius: 15rpx;
		font-size: 32rpx;
		text-align: center;
		padding: 12rpx 0;
		color: #fff;
	}
}
.share-maske {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(0, 0, 0, 0.6);
	z-index: 99999;
	.share-tip {
		position: absolute;
		top: 100rpx;
		left: 0;
		text-align: center;
		.share-tip-info {
			height: 240rpx;
		}
		.share-tip-btn {
			margin-top: 400rpx;
			height: 82rpx;
		}
	}
	.share-browser-tip {
		top: 300rpx;
		.share-tip-info {
			height: 240rpx;
		}
		.share-tip-btn {
			margin-top: 200rpx;
		}
	}
}
.declaration {
	margin: 50rpx 0;
	font-size: 34rpx;
	color: #999;
}
</style>
