<template>
	<view class="container">
		<view class="content">
			<view class="detail">
				<view class="comment">
					<view class="item">
						<view class="avatar"><image :src="info.avatar_url"></image></view>
						<view class="comment-info">
							<view class="nickname-like">
								<view class="nickname">{{ info.nickname }}</view>
								<view class="like">
									<image @tap="commentLike()" v-show="!info.is_like" src="/static/images/icon_like_01.png"></image>
									<image @tap="commentLike()" v-show="info.is_like" src="/static/images/icon_like_01_hover.png"></image>
									<view class="like-count">{{ info.like_count }}</view>
								</view>
							</view>
							<view class="comment-desc">{{ info.content }}</view>
							<view class="date-reply">
								<view class="date">{{ info.create_time }}</view>
								<view @tap="initAddReply('commit', info.id, info.nickname)" class="reply">回复TA</view>
							</view>
						</view>
					</view>
				</view>
				<view class="comment replys">
					<view class="title">
						全部回复
						<text v-if="info.reply.count > 0">({{ info.reply.count }})</text>
					</view>
					<view class="item" v-for="(item, index) in info.reply.list" :key="index">
						<view class="avatar"><image :src="item.avatar_url"></image></view>
						<view class="comment-info">
							<view class="nickname-like">
								<view class="nickname">{{ item.nickname }}</view>
								<view class="like">
									<image @tap="replyLike(item.id, index)" v-show="!item.is_like" src="/static/images/icon_like_01.png"></image>
									<image @tap="replyLike(item.id, index)" v-show="item.is_like" src="/static/images/icon_like_01_hover.png"></image>
									<view class="like-count">{{ item.like_count }}</view>
								</view>
							</view>
							<view class="comment-desc">
								<text>{{ item.pid > 0 ? '回复@' + item.parent_nickname + '：' : '' }}</text>
								{{ item.content }}
							</view>
							<view class="reply-count" v-if="item.reply_count > 0">全部{{ item.reply_count }}条回复 ></view>
							<view class="date-reply">
								<view class="date">{{ item.create_time }}</view>
								<view @tap="initAddReply('reply', item.id, item.nickname)" class="reply">回复TA</view>
							</view>
						</view>
					</view>
					<loading v-if="hasMoreData"></loading>
					<view class="no-comment" v-if="info.reply.count == 0">暂无回复，快来抢占沙发吧~</view>
				</view>
			</view>
			<view class="float-empty"></view>
			<view class="bg-masker" v-if="showAddReply" @tap="showAddReplyBg(false)"></view>
			<view class="add-comment" v-if="!showPageLoading">
				<view class="info">
					<view class="input">
						<textarea
							:cursor-spacing="20"
							ref="commentContent"
							:show-confirm-bar="false"
							@focus="showAddReplyBg(true)"
							:maxlength="200"
							:focus="addReplyFocus"
							:fixed="true"
							:auto-height="true"
							v-model.trim="commentContent"
							placeholder-class="input-bg"
							:placeholder="replyPid > 0 ? '回复 ' + replyNickname + '：' : '回复 ' + info.nickname + '：'"
						/>
					</view>
					<view class="add"><button class="mini-btn" type="primary" size="mini" @click="addReply()">回复</button></view>
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
			info: { reply: { count: 0, list: [] } },
			showAddReply: false,
			addReplyFocus: false,
			operate: '',
			commentContent: '',
			replyCommentId: 0,
			replyPid: 0,
			replyNickname: '',
			page_index: 1,
			page_size: 10,
			hasMoreData: false,
			showPageLoading: true
		};
	},
	onShow() {
		this.$initPageTitle(); //初始化页面标题
	},
	onLoad(e) {
		if (e.id > 0) {
			this.id = e.id;
		}
		if (e.operate == 'reply') {
			this.operate = e.operate;
		}
		this.getCommentDetail();
	},
	onPullDownRefresh() {
		uni.showLoading({
			title: '刷新中'
		});
		this.page_index = 1;
		this.getCommentDetail();
	},
	onReachBottom: function() {
		console.log(this.hasMoreData);
		if (this.hasMoreData) {
			this.getReply();
		}
	},
	methods: {
		/*获取评论*/
		getCommentDetail() {
			this.$app.request({
				url: this.$api.article.commentDetail,
				data: {
					id: this.id,
					page_index: this.page_index,
					page_size: this.page_size
				},
				method: 'POST',
				dataType: 'json',
				success: res => {
					if (res.code == 0) {
						this.info = res.data;
						this.showPageLoading = false;
						if (res.data.reply.page > this.page_index) {
							this.hasMoreData = true;
						} else {
							this.hasMoreData = false;
						}
						this.page_index += 1;

						/*初始化回复*/
						if (this.operate == 'reply') {
							this.showAddReplyBg(true);
						}
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

		/*评论点赞*/
		commentLike() {
			this.$app.request({
				url: this.$api.article.commentLike,
				data: {
					comment_id: this.id
				},
				method: 'POST',
				dataType: 'json',
				success: res => {
					console.log('res');
					if (res.code == 0) {
						if (this.info.is_like) {
							this.info.is_like = false;
							this.info.like_count -= 1;
						} else {
							this.info.is_like = true;
							this.info.like_count += 1;
						}

						let msg = this.info.is_like ? '点赞成功' : '已取消点赞';
						this.$alert(msg,'success');
					} else {
						this.$alert(res.msg,'warning');
					}
				}
			});
		},

		/*回复点赞*/
		replyLike(reply_id, index) {
			this.$app.request({
				url: this.$api.article.commentLike,
				data: {
					comment_id: reply_id
				},
				method: 'POST',
				dataType: 'json',
				success: res => {
					if (res.code == 0) {
						if (this.info.reply.list[index].is_like) {
							this.info.reply.list[index].is_like = false;
							this.info.reply.list[index].like_count -= 1;
						} else {
							this.info.reply.list[index].is_like = true;
							this.info.reply.list[index].like_count += 1;
						}
						let msg = this.info.reply.list[index].is_like ? '点赞成功' : '已取消点赞';
						this.$alert(msg,'success');
					} else {
						this.$alert(res.msg,'warning');
					}
				}
			});
		},

		/*初始化回复*/
		initAddReply(type, id, nickname) {
			this.replyNickname = nickname;
			if (type == 'reply') {
				this.replyPid = id;
			}
			this.showAddReply = true;
			this.addReplyFocus = true;
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
					comment_id: this.id,
					pid: this.replyPid,
					content: this.commentContent,
					page_size: this.page_size
				},
				method: 'POST',
				dataType: 'json',
				success: res => {
					if (res.code == 0) {
						this.info.reply = res.data;
						this.replyPid = 0;
						this.replyNickname = '';
						this.commentContent = '';
						this.showAddReply = false;
						this.addReplyFocus = false;
						this.$alert('回复成功','success');
					} else {
						this.$alert(res.msg,'warning');
					}
				}
			});
		},

		/*展示回复背景*/
		showAddReplyBg(status) {
			this.showAddReply = status;
			console.log(status);
			setTimeout(() => {
				this.addReplyFocus = status;
			}, 200);
		},

		/*获取回复*/
		getReply() {
			this.$app.request({
				url: this.$api.article.commentReply,
				data: {
					comment_id: this.id,
					page_index: this.page_index,
					page_size: this.page_size
				},
				method: 'POST',
				dataType: 'json',
				success: res => {
					if (res.code == 0) {
						console.log(this.info.reply);
						this.info.reply.list = this.info.reply.list.concat(res.data.list);
						this.info.reply.count = res.data.count;
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
		}
	}
};
</script>

<style scoped lang="scss">
.comment {
	padding: 15rpx 32rpx 20rpx;
	background: #fff;
	.title {
		display: flex;
		align-items: center;
		font-size: 34rpx;
		color: #444;
		font-weight: bold;
		padding-bottom: 15rpx;
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
				color: #262626;
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
.replys {
	border-top: 0;
	margin-top: 15rpx;
	padding-bottom: 0;
}
.comment-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	width: 100%;
	height: 120rpx;
	background-color: #ffffff;
	border-top: 1rpx solid #ddd;
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
			font-size: 30rpx;
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
				right: -9rpx;
				background: #fd1815;
				display: block;
				border-radius: 20rpx;
				padding: 2rpx 10rpx;
				font-size: 24rpx;
				line-height: 1.3;
				color: #fff;
			}
		}
		.share {
			display: flex;
			justify-content: space-around;
			align-items: center;
			width: 25%;
			image {
				width: 45rpx;
				height: 45rpx;
			}
			&:after {
				display: none;
			}
			&:last-child {
				image {
					border: 1rpx solid #63b5e8;
				}
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
	box-shadow: 0 1px 1px 1px rgba(0, 0, 0, 0.2);
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
			margin-left: 15rpx;
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
