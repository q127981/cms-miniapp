<template>
	<view class="global-loading-container global-loading-mask">
		<view class="global-loading-wrapper">
			<view class="global-loading"></view>
		</view>
	</view>
</template>
<script>
	export default {
		name: 'pageLoading'
	}
</script>
<style lang="scss">
	.global-loading-container {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(255, 255, 255, .5);
		
		
		
	}
	
	.global-loading-container.global-loading-mask {
		background: #fff;
	}
	
	.global-loading-wrapper {
		position: relative;
		height: 100%;
		width: 100%;
	}
	
	.global-loading {
		position: absolute;
		top: 45%;
		left: 50%;
		height: 135rpx;
		width: 135rpx;
		transform: translate(-50%, -50%);
	}
	
	.global-loading:before,
	.global-loading:after {
		content: '';
		display: inline-block;
		position: absolute;
		top: 0;
		left: 0;
		height: 135rpx;
		width: 135rpx;
		box-sizing: border-box;
	}
	
	@-webkit-keyframes globalLoadingRotate {
		from {
			transform: rotate(0);
		}
	
		to {
			transform: rotate(360deg);
		}
	}
	
	@keyframes globalLoadingRotate {
		from {
			transform: rotate(0);
		}
	
		to {
			transform: rotate(360deg);
		}
	}
	
	.global-loading:before {
	    border: 4rpx solid #f7f7f7;
	    border-right-color: #ffd100;
	    border-radius: 100%;
	    animation: globalLoadingRotate linear 1.5s infinite;
	}
		
	.global-loading:after {
	    // background: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAFmklEQVR4Xu1bXWwUVRg93+xCWyMokWpiUkQFAvGPGJSdbYg0Iamd2RqtIVFREzSaGMhG6SygiRb0AWVnCyFiJAbE+ECEqJHdWQJGywPd2Qr4QNAgxqgUnzSkgFrAcj8z/MT+7czdaQdZOvO499xzzj33m7l3d+4SxvhFY3z8CAMIK2CMJxDeAn4LYO2B/J3VfTibjGnH/XKMRr8N3+Zrz51DrRHTvvfDV3YFZIrWEmYsBTDzgiCRTcD21pi23o8Bv30yxazGrLwOIHbRBn3HgrNGXH+1HM6yAjBti0uSM7YZcf2pcsT9YtN2ziKQVqq/oerS45IGmgVrDQgrXU2TMs+INe3zOzCZfqa9qwUQn7phibC0NaZvlOGTD8C2DgG4x4N0q6Hqi2WE/WLMQn4TiF/06H/EUPVZMhpSAayxd00dB/GzBOEvhqrfLoHzDTHt3I8ATfMiEONo2vI52k9eOKkA0nZ2PkHp8CJz2su5/2T4BmNcn0P9wAzRkFKb93pphAF4JeS0hxUQ3gLhMyB8CIarwFheBtsOHx4/4fSvPQBq3FcN2muoWoPMyuIXY9pWHkCTR/8z4yfTjcnp2lkvHal9gENiFqwcCLobIQuxOlXfvMpLdCTt6c7sKlKUNtfvAqDPW1WtRUZHOgCJvcBpQ9UnyoiOFGPa+V6Aq0vycHSWEW88IqMjHYBDlumyEixoE8C3DiAnHMV55Wmjvmm/jOhIMZkua8YlH/MHcZ0E8WtGLPGerEZZATik7+zbNyESObmIgXoCThHz3tZ4Yoes4Gji0p25J0iheQzUgnl3VbWyM3m/9ns5GmUHUA55JWDDACphloL0GFZAkOlWAndYAZUwS0F6DCsgyHQrgTusgEqYpSA9hhUQZLqVwB1WQCXMUpAeh62Aiz870SJAOQ7BW4x6/eMgTQTFbXZaz7CCZgJOCqBzuapvHaw1JIBMMb+OmV/uD2Tmt1LxxBtBGQ2CN13IvUlEzgmS/tdaQ9VX9P9gSACmbTmvwacONsVMZiqupYIwO9qc6UI+TcTGkDEAx1OqXucaQKaYP8zMdw1rirHRiOvO+aCr9jIL1rsgLCllcPDr+yEV4PmzM9EWI6Y9fzUmYBbzm8H8nIu39w1Vf8m1ApxG07a+BLCgJBFhG6Iiacxp/uNqCMI8kJ2MPmUDGE+W9kPZ02dqWlY3NPR5BnAhhKL1AxgzXAZ4lCHMlNr8wf8ZQtrOvkBQnPvdzesJhSPTl8UfPuG5CvQHeL6AAMCgvELCbI0lpI7QjFZYmWKuQbBiELjkcbnLWqycvyM195Fhzzh57gRN2yoCmOtlnIANiETN1gcbu72wI2nPfLO7Duf7DAaSMjwMxFKq3lUK6xnApWdCO4BXvAXJKbE9LHgPro9uT93X+Jd3H29Ee6FQwziREKAEEekA3+TdC4dYiT6amtvoerpNKgBH7OLqEFnh+k5uoKteYuxh4BMjrm+TMDwEYtq5FgI9JoAWAq6T5iDawjWRpMwESAfgiLd3WXOYsZIZj0ub+Q/YTcAxBncDdIyZuhWiY06zYJ5CxHUATyFQHQNTAAzYsMjp0X6A3jbUps/k8PD3j5H2orVYMJzt8r2yQgHjTrEQ66pujqyRORMgtQx6GW7r+LB6YlXtUiZydoa3eeGDaidgMxO1X7Hj8oMHsr74xS3/iOizRFgI4IGgBjpw1uhPJs5C8EdGPLF7JJplPQO8hDLFXRqzWMgMnQi1Xvgy23sBOEdfdyosssvizb+V2X9Y+KgGcFmhraMjekPV3wsE0QIifogZswFEfRg+COArMH89XlHsZEw75YPDtUsgAQynmO7M3Y0IZoOVmQSeBMYkJkxSnN0kUQ+Ye0DoAdhZtw8asYQz+MCvKxZA4CPxKRAG4DO4a6ZbWAHXzFT6HMiYr4B/AXKuDF/iL7LlAAAAAElFTkSuQmCC") center/64rpx 64rpx no-repeat;
	}
</style>
