<template>
	<view :class="'icon iconfont icon-'+type"></view>
</template>
<script>
	export default {
		name: 'iconfont',
		props: {
			type: String,
		}
	}
</script>
