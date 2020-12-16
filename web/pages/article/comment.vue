<template>
	<view class="container">
		<view class="content">
			<view class="detail">
				<view class="comment">
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
			</view>
			<view class="float-empty"></view>
			<view class="bg-masker" v-if="showAddComment" @tap="initAddComment(false)"></view>
			<view class="add-comment" v-if="!showPageLoading">
				<view class="info">
					<view class="input">
						<textarea
							:cursor-spacing="20"
							ref="commentContent"
							:show-confirm-bar="false"
							@focus="initAddComment(true)"
							:maxlength="200"
							:focus="addCommentFocus"
							:fixed="true"
							:auto-height="true"
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
		</view>
		<pageLoading v-if="showPageLoading"></pageLoading>
	</view>
</template>

<script>
import pageLoading from '@/components/loading/pageLoading.vue';
import iconfont from '@/components/iconfont/iconfont.vue';
import loading from '@/components/loading/loading.vue';
export default {
	components: {
		pageLoading,
		loading,
		iconfont
	},
	data() {
		return {
			id: 0,
			info: { comment: { count: 0, list: [] } },
			showAddComment: false,
			addCommentFocus: false,
			commentContent: '',
			replyCommentId: 0,
			replyNickname: '',
			page_index: 1,
			page_size: 10,
			hasMoreData: false,
			showPageLoading: true
		};
	},
	onShow() {
		this.$initPageTitle(); //初始化页面标题
		if (this.page_index > 1) {
			this.page_index = 1;
			this.getComment();
		}
	},
	onLoad(e) {
		if (e.article_id > 0) {
			this.id = e.article_id;
		}
		this.getComment();
	},
	onPullDownRefresh() {
		uni.showLoading({
			title: '刷新中'
		});
		this.page_index = 1;
		this.getComment();
	},
	onReachBottom: function() {
		console.log(this.hasMoreData);
		if (this.hasMoreData) {
			this.getComment();
		}
	},
	methods: {
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
						if (this.page_index == 1) {
							this.info.comment.list = [];
							this.info.comment.count = 0;
						}
						this.info.comment.list = this.info.comment.list.concat(res.data.list);
						this.info.comment.count = res.data.count;
						console.log(res.data.count);
						uni.setNavigationBarTitle({
							title: '全部评论(' + res.data.count + ')'
						});
						this.$initPageTitle();
						this.showPageLoading = false;
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
							this.$alert(msg, 'success');
						} else {
							this.$alert(res.msg, 'warning');
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
						this.$alert(msg);
					} else {
						this.$alert(res.msg);
					}
				}
			});
		},

		/*初始化添加评论*/
		initAddComment(status) {
			this.showAddComment = status;
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
						uni.setNavigationBarTitle({
							title: '全部评论(' + res.data.count + ')'
						});
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
		}
	}
};
</script>

<style scoped lang="scss">
.comment {
	padding: 0 32rpx;
	background: #fff;
	.title {
		display: flex;
		align-items: center;
		font-size: 34rpx;
		color: #262626;
		font-weight: bold;
		padding-bottom: 20rpx;
		border-bottom: 1rpx solid #eee;
		text {
			font-size: 28rpx;
			color: #999;
			margin-left: 10rpx;
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
				width: 56rpx;
				height: 56rpx;
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
						width: 30rpx;
						height: 30rpx;
						margin-right: 14rpx;
					}
					.like-count {
						color: #999;
						font-size: 28rpx;
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
		padding: 20rpx 0;
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
	box-shadow: 0 1px 1px 1px rgba(0, 0, 0, 0.4);
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
				background-image: linear-gradient(140deg, #BEEDC7 9%, #ffd100 75%);
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
	box-shadow: 0 1px 1px 1px rgba(0, 0, 0, 0.2);
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
</style>
