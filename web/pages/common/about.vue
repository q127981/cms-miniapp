<template>
	<view class="page">
		<view class="content">
			<view class="ul">
				美团团节社是什么？
				<view class="li">
					“美团团节社”是美团在2020年底推出的吃喝玩乐分享平台，该项目将于10月底以邀请制的形式对外开放，第一批试点将在西安进行，第二批则在武汉开放，第三批会选择在成都。该项目定位的是C端用户市场，由美团一位副总裁直管的新业务部门直接操刀，重视程度可见一斑。
				</view>
			</view>
			<view class="ul">
				美团团节社有哪些赚钱方法？
				<view class="li">
					当团节社的社长，通过分享商品、邀好友注册获得一笔相应的佣金和平台奖励。
					成为团节社平台的达人，达人可以自己组建团队，当团队下的团员分享出去了商品、邀请了好友，那么达人也能有一笔分佣收益，当然达人自己也可以通过分享商品、邀好友来赚钱。
				</view>
			</view>
			<view class="ul">
				扫描首页邀请码，加入成为社长吧!
			</view>
		</view>
	</view>
</template>

<script>
	import iconfont from '@/components/iconfont/iconfont.vue'
	export default {
		components: {
			iconfont
		},
		data() {
			return {

			}
		},
		onShow() {
			this.$initPageTitle(); //初始化页面标题
		},
		onLoad(e) {

		},
		onPullDownRefresh() {

		}
	}
</script>

<style scoped lang="scss">
	.ul {
		margin: 30upx 40upx 20rpx;
		font-size: 34rpx;
		color: #FF3333;
		font-weight: bold;
	}
	.li {
		margin: 30upx 40upx 20rpx;
		font-size: 32rpx;
		color: #000;
	}
</style>
