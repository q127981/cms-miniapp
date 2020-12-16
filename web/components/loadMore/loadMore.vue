<template>
	<view class="load-more" v-if="status === 1">
		<image 
			class="load-icon"
			src="data:image/png;base64,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"
			v-show="status === 1"
		>
		</image>
		<text class="load-text">{{text[status]}}</text>
	</view>
</template>

<script>
	export default {
		name: "load-more",
		props: {
			status: {
				//0加载前，1加载中，2没有更多了
				type: Number,
				default: 0
			},
			text: {
				type: Array,
				default () {
					return [
						'上拉显示更多',
						'加载中…',
						'没有更多数据了'
					];
				}
			}
		}
	}
</script>

<style>
	.load-more{
		display: flex;
		flex-direction: row;
		justify-content: center;
		align-items: center;
		height: 90rpx;
		padding-top: 10rpx;
	}
	.load-icon{
		display: block;
		width: 36rpx;
		height: 36rpx;
		margin-right: 12rpx;
		animation: load 1.2s cubic-bezier(.37,1.08,.7,.74) infinite;
	}
	.load-text{
		font-size: 28rpx;
		color: #ffd100;
	}

	@-webkit-keyframes load {
		0% {
			transform: rotate(0deg);
		}

		100% {
			transform: rotate(360deg);
		}
	}
</style>