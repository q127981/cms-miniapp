import Vue from 'vue'
import App from './App'
import store from './store'
import api from './config/api'
import base from './common/app'
import h5Api from './common/sdk/H5Api'

Vue.config.productionTip = false

Vue.prototype.$store = store
Vue.prototype.$api = api
Vue.prototype.$app = base

/*无状态提示信息*/
Vue.prototype.$alert = function(msg = '', icon = 'none', url = '', openType = 'navigate') {
	base.alert(msg, icon, url, openType);
};

/*弹出加载框*/
Vue.prototype.$loading = function(msg = '', mask = true) {
	base.loading(msg, mask);
};

/*是否登录*/
Vue.prototype.$initLogin = function() {
	if (!uni.getStorageSync('isLogin')) {
		base.login();
	}
};

/*初始化页面标题*/
Vue.prototype.$initPageTitle = function(pageTitle = '团节社成都') {
	// #ifdef H5
	document.title = pageTitle;
	// #endif
};

App.mpType = 'app'

const app = new Vue({
	...App
})
app.$mount()
