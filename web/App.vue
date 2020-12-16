<script>
export default {
	onLaunch: function() {
		console.log('App Launch');
	},
	onShow: function() {
		console.log('App Show');
		this.$initPageTitle(); //初始化页面标题
	},
	onHide: function() {
		console.log('App Hide');
	}
};
</script>

<style lang="scss">
/*每个页面公共css */
@import './common/uni.css';
@import './common/iconfont.css';
@import './common/app.scss';
</style>
