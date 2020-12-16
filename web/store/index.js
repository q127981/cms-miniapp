import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)
const store = new Vuex.Store({
	state: {
		isLogin: false,
		currentUser: {}
	},
	mutations: {
		login(state, userInfo) {
			state.isLogin = true;
			state.currentUser = userInfo;
			uni.setStorageSync("isLogin", '1');
			uni.setStorageSync("accessToken", userInfo.token);
			uni.setStorageSync('currentUser', userInfo);
		},
		logout(state) {
			state.isLogin = false;
			state.currentUser = {};
			uni.removeStorageSync('isLogin');
			uni.removeStorageSync('accessToken');
			uni.removeStorageSync('currentUser');
			console.log('退出登录')
		}
	}
})

export default store
