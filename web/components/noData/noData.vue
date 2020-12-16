<template>
	<view class="no-data">
		<view class="no-data-icon">
			<image :lazy-load="true" :src="src"></image>
		</view>
		<view class="no-data-text">
			{{text}}
		</view>
	</view>
</template>
<script>
	export default {
		name: 'noData',
		props: {
			src: {
				type: String,
				default: '/static/images/icon_no_data_02.png'
			},
			text: {
				type: String,
				default: '暂无~'
			},
		},
	}
</script>
<style lang="scss">
	.no-data {
		display: flex;
		flex-direction: column;
		align-items: center;
		position: fixed;
		top: 30%;
		left: 50%;
		height: 135rpx;
		width: 235rpx;
		transform: translate(-50%, -50%);

		.no-data-icon {
			image {
				width: 80rpx;
				height: 80rpx;
			}
		}

		.no-data-text {
			color: #bfbfbf;
			font-size: 30rpx;
		}
	}
</style>
