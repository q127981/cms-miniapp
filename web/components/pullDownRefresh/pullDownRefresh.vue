<template>
	<!-- #ifdef H5	 -->	
	<view 
		class="refresh-content"
		:style="{
				transform: 'translateY('+ pageDeviation +'px)',
				transition: pageTransition + 's',
				height: 'calc(100% - ' + pageTop + 'px)',
				maxHeight: 'calc(100% - ' + pageTop + 'px)'
			}"
		@touchstart="pageTouchstart"
		@touchmove="pageTouchmove"
		@touchend="pageTouchend"
	>
	<!-- #endif -->
	<!-- #ifndef H5	 -->	
	<view 
		class="refresh-content"
		:style="{
				transform: 'translateY('+ pageDeviation +'px)',
				transition: pageTransition + 's',
				height: 'calc(100vh - ' + pageTop + 'px)',
				maxHeight: 'calc(100vh - ' + pageTop + 'px)'
			}"
		@touchstart="pageTouchstart"
		@touchmove="pageTouchmove"
		@touchend="pageTouchend"
	>
	<!-- #endif -->
		<!-- 下拉刷新 -->
		<view class="loading-wrapper">
			<image 
				class="loading-icon" 
				:class="{active: refreshing, ready: refreshReady}" 
				src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAEwklEQVR4Xu1aS1oTQRD+KwvDTjyBcALhBMAJJBtNVsIJwBMYTyCcwLBKdEM4gXACkxMYTmCW6oL2q57Jc6pf82LyZXo7PV3Vf/1dr27Clg/a8v2jBqBmwJYjUB+BLSdA7QTrI1DkEVDfcYAnHIGwC4XjIFmEeyhMoXBHHUyC/g2YnDsD1C128Q8XUDgDsBegi23qBIQeXuCaWpjmtKZeJjcAljZ+CWA3TyWX1pqCcJUnELkAEFP9NkeLu/AboYFzeoeRa6Lre2YAVB9nIHwp0OqmPbB/aFEH965N2r5nAkB9wykU2PKmMQbQg8IIOxj5nl99nP7gAMAxCKcA3hglKJxkASE1ADHtf4iWV3gA0M2i2PKGVV9HkB4IrwUgpmjgJO1xSAWAttBf/DLQ/praYEeY+1B9DcIHYeERtXGYRmA6AProgvApIVDhnDropVHE9x810OCyz1kfH6mNK991ZvOCAVB97IHwM2F9hc/UQTdUgTTzDUyYool9Xz+THoCBRvliTfExtbXTKm2oPjg5WvcJwSwIZ8BAn/3VDC+jJ06DmiECBfuCIABiz8/0Xx6lW38mXGSBwn5I7RAGgOyAgmmXxuLSP0o6joQWvcfQV0YYAJL3fwb6zxkgJWKBzjgUgGQcDqScr2V85ukEicDJ2GIUDMA9CEdrynEuHlbr++zObw4XQ6vRR+GBOv76eDMgzv4Y7VLDnR8OK7OCIoEXADHVvpZY7qbY98ovEzTQ8qkPnABYUs+sShb/v0dqbgUgrvXZ8ps7HCAYAfCo9TcHFEuoFgFwlLubs/GFphNqY19SXAbAVO5u4tbnWZNcrcoADPDb0eMbQ+mqMOrHNXT//1LIEYqBjDtODVzhad4U5TyEexRSx2img8iCBABOx6dwQx3d808MS8cmPyAM8uNjy82Yt0Zhgi9IAiDX+9GaCo/UsV92qIG2irmJmQUKR5YXg8C3SC9FMUKaLDFASndn6zkrPyeDsgHgbLmJFeLCDyTSZIkBXO/L6a5H5ScWKFk2vfyvj3xbq15gkAQA372ZKOTswT87AOamKR9hDwb0YTsCzpa3KjaE+hxBU+vcE4CB7qaYPClfRx2aWk4lJFATNHFo6vwaWnaLQyREEOkImPrus4XEi8m4Xc7XZEWXyyM0cbIOgvWmauEEE040CUD0qGG98Sm5MaZa9HBB6dDId3hFXYuvy2c/xUwdxY8vGHSWbx9C98qUCtv8gEtMNb8bEigTAMleWzW35a9VA4dSg8RcDtudob/gasw0Ri8zANENMBc7xaS15QEzRhPHpshh7whFDxXY0diqrPK2Ei7Junlezt0TjEAYllbqhm/S9Mcdmjhz3RY7AZiH0CjF5OtvOU3OT/FsKyk8xq9TvN4peAOgw33EhlOQBqNaviF6ltPDDoYuqy8jHATAnA3SlZRkN4UbIPMrT36QIT2LWZXoUSlKKhYNgLN6dPHdu7qsAUj3XC4dA3zrhSZehZxH8RRFb5L4VUpwnu/6xSsMmhZR9r4B/+bsHfgoqJ2vOyu9o7ZHMSQITMWApYjArXGuGxaJEochwjDPt4Jxn6ELpSNQQhaa6KZlWmoAfK1X9Xk1AFW3UNH61QwoGuGqr18zoOoWKlq//7Yjw1CiDj3rAAAAAElFTkSuQmCC">
			>
			</image>
			<text :class="{refreshing: refreshing, ready: refreshReady}" >{{refreshing?'刷新中…':'下拉刷新~'}}</text>
		</view>
		<slot></slot>
	</view>
</template>

<script>
	let startY, moveY, windowHeight = 500, platform;
	let timeDiff = 0;
	let touchending;
	export default {
		
		props: {
			top: {
				//距离顶部距离，单位upx
				type: Number,
				default: 0
			},
		},
		data() {
			return {
				pageDeviation: 0, //下偏移量
				pageTransition: 0, //回弹过渡时间
				refreshReady: false, //进入刷新准备状态
				refreshing: false, // 进入刷新状态
			};
		},
		computed: {
			pageTop(){
				return uni.upx2px(this.top);
			}
		},
		created(){
			uni.getSystemInfo({
				success: function(e) {
					console.log(e);
					platform = e.platform;
					windowHeight = e.windowHeight;
				}
			})
		},
		methods: {
			pageTouchstart(e){
				touchending = false;
				this.pageTransition = 0;
				startY = e.touches[0].pageY;
			},
			pageTouchmove(e){
				if(touchending){
					return;
				}
				moveY = (e.touches[0].pageY - startY) * 0.4;
				if(moveY >= 0){
					this.pageDeviation = moveY;
					
					this.$emit('setEnableScroll', false);
				}
				if(moveY >= 50 && this.refreshReady === false){
					this.refreshReady = true;
				}else if(moveY < 50 && this.refreshReady === true){
					this.refreshReady = false;
				}
				if(platform === 'ios' && e.touches[0].pageY > windowHeight + 10){
					this.pageTouchend();
				}
			},
			pageTouchend(){
				touchending = true;
				if(moveY === 0){
					return;
				}
				this.pageTransition = 0.3;
				if(moveY >= 50){
					this.startPulldownRefresh();
				}else{
					this.pageDeviation = 0;
				}
				
				if(this.refreshReady === true){
					this.refreshReady = false;
				}
				//修复下拉一点回弹后页面无法滚动的bug
				this.$emit('setEnableScroll', true);
				startY = moveY = 0;
			},
			//开启下拉刷新
			startPulldownRefresh(){
				if(+new Date() - timeDiff < 100){
					return;
				}
				timeDiff = +new Date();
				this.refreshing = true;
				this.pageDeviation = uni.upx2px(90);
				this.$emit('refresh');
			},
			//结束下拉刷新
			endPulldownRefresh(){
				this.refreshing = false;
				this.pageDeviation = uni.upx2px(0);
				//this.$emit('setEnableScroll', true);
			},
		}
	}
</script>

<style>
	.refresh-content{
		display: flex;
		flex-direction: column;
		position: relative;
	}
	/* 下拉刷新部分 */
	.loading-wrapper{
		position: absolute;
		left: 0;
		top: 0;
		transform: translateY(-100%);
		display: flex;
		justify-content: center;
		align-items: center;
		width: 100%;
	}
	.loading-wrapper text{
		color: #ffd100;
		padding-left: 20rpx;
	}
	.loading-icon{
		width: 70rpx;
		height: 70rpx;
		transition: .3s;
	}
	.loading-icon.ready{
		transform: scaleX(1.3);
	}
	.loading-icon.active,.refreshing{
		animation: loading .5s ease-in infinite both alternate;
	}
	
	@keyframes loading {
		0% {
			transform: translateY(-20upx) scaleX(1);
		}
		100% {
			transform: translateY(4upx)  scaleX(1.3);
		}
	}
</style>
