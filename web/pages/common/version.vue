<template>
	<view class="page">
		<view class="content">
			<view class="logo">
				<image :lazy-load="true" src="../../static/images/logo.png"></image>
			</view>
			<view class="version">V1.1</view>
		</view>
	</view>
</template>

<script>
	import iconfont from '@/components/iconfont/iconfont.vue'
	export default {
		components: {
			iconfont
		},
		onShow() {
			this.$initPageTitle();//初始化页面标题
		},
		data() {
			return {
			}
		},
		onLoad(e) {

		},
		onPullDownRefresh() {

		}
	}
</script>

<style scoped lang="scss">
	.content {
		display: flex;
		flex-direction: column;
		justify-content: center;
	
		.logo {
			margin-top: 143rpx;
			margin-left: auto;
			margin-right: auto;
	
			image {
				width: 218rpx;
				height: 219rpx;
			}
		}
		
		.version{
			text-align: center;
			font-size: 36rpx;
			color: #f9a951;
		}
	}
</style>
