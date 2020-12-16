<template>
	<view class="search">
		<view class="searh-header">
			<!-- #ifdef H5 -->
			<navigator class="back" open-type="navigateBack" hover-class="none"><iconfont type="back"></iconfont></navigator>
			<!-- #endif -->
			<view class="box">
				<view class="input">
					<iconfont type="search" class="search-icon"></iconfont>
					<input focus type="text" v-model.trim="curKeyword" placeholder="搜索你想看的…" @confirm="search('')" @focus="initSearch" />
				</view>
				<view class="clear" v-if="curKeyword != ''">
					<view @tap="clearSearch()"><iconfont type="cancel" class="cancel-icon"></iconfont></view>
				</view>
			</view>
		</view>
		<view class="history" v-if="history.length > 0 && showHistory">
			<view class="title">
				<text>搜索历史</text>
				<view @tap="clearHistory()"><iconfont type="remove"></iconfont></view>
			</view>
			<view class="list">
				<view class="item" v-for="(item, index) in history" :key="index" @tap="search(item)">{{ item }}</view>
			</view>
		</view>
	</view>
</template>
<script>
import iconfont from '@/components/iconfont/iconfont.vue';
export default {
	name: 'search',
	data() {
		return {
			curKeyword: '',
			curHistory: [],
			curshowHistory: true
		};
	},
	components: {
		iconfont
	},
	props: {
		name: {
			type: String,
			default: ''
		},
		keyword: {
			type: String,
			default: ''
		},
		history: {
			type: Array,
			default: function(e) {
				return [];
			}
		},
		showHistory: {
			type: Boolean,
			value: false
		}
	},
	watch: {
		keyword: function(newVal, oldVal) {
			this.curKeyword = newVal;
		},
		curKeyword: function(newVal, oldVal) {
			console.log(newVal);
			this.$emit('update:keyword', newVal);
		},
		history: function(newVal, oldVal) {
			this.curHistory = newVal;
		},
		curHistory: function(newVal, oldVal) {
			this.$emit('update:history', newVal);
		},
		showHistory: function(newVal, oldVal) {
			this.curshowHistory = newVal;
		},
		curshowHistory: function(newVal, oldVal) {
			this.$emit('update:showHistory', newVal);
		}
	},
	methods: {
		search(str) {
			if (str != '') {
				this.curKeyword = str;
			}
			if (this.curKeyword != '') {
				this.$emit('confirm', str);
			}
		},
		initSearch() {
			this.$emit('focus');
		},
		clearSearch() {
			this.curKeyword = '';
			this.curshowHistory = true;
		},
		clearHistory() {
			uni.showModal({
				title: '提示',
				content: '确认删除吗？',
				success: res => {
					if (res.confirm) {
						this.curHistory = [];
						console.log(this.name)
						uni.removeStorageSync(this.name);
					} else if (res.cancel) {
						console.log('用户点击取消');
					}
				}
			});
		}
	}
};
</script>
<style lang="scss" scoped>
/*搜索*/
.search {
	margin: 24rpx 24rpx 0 24rpx;
	.searh-header {
		display: flex;
		align-items: center;
		.back {
			flex-grow: 0;
			flex-shrink: 0;
			margin-right: 30rpx;
			/deep/.icon {
				color: #c1c4c9;
				font-size: 36rpx;
			}
		}
		.logo {
			flex-grow: 0;
			flex-shrink: 0;
			height: 40rpx;
			width: 160rpx;
			margin-right: 30rpx;
		}
		.box {
			flex-grow: 1;
			flex-shrink: 1;
			display: flex;
			flex-direction: row;
			height: 70rpx;
			background: #f5f5f5;
			border-radius: 34rpx 34rpx 0 34rpx;
			.input {
				flex-grow: 1;
				flex-shrink: 1;
				margin-left: 0 !important;
				display: flex;
				flex-direction: row;
				align-items: center;
				image {
					margin-left: 32rpx;
					width: 32rpx;
					height: 32rpx;
				}
				/deep/.icon,
				.search-icon {
					margin-left: 32rpx;
					color: #aaaaaa;
					font-size: 32rpx;
				}

				input {
					margin-left: 16rpx;
					font-size: 30rpx;
					width: 86% !important;
					line-height: normal;
				}
			}
			.clear {
				flex-grow: 0;
				flex-shrink: 0;
				display: flex;
				flex-direction: row;
				align-items: center;
				width: 60rpx;
				image {
					width: 20rpx;
				}
				.cancel-icon {
					color: #999;
					font-size: 30rpx;
				}
			}
		}
	}
	.history {
		margin-top: 20rpx;
		padding: 10rpx;
		.title {
			display: flex;
			flex-direction: row;
			justify-content: space-between;
			align-items: center;
			text {
				font-size: 32rpx;
				font-weight: bold;
				color: #000;
			}
			/deep/.icon {
				font-size: 36rpx;
				color: #999999;
			}
		}
		.list {
			display: flex;
			flex-direction: row;
			flex-wrap: wrap;
			margin-top: 15rpx;
			.item {
				border-radius: 30rpx;
				padding: 10rpx 30rpx;
				font-size: 28rpx;
				background: #f2f2f2;
				margin-right: 40rpx;
				margin-top: 20rpx;
				&:last-child {
					margin-right: 0;
				}
			}
		}
	}
}
</style>
