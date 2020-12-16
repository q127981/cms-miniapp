// let apiRoot = 'http://tcyhw.free.idcfengye.com/api/';
let apiRoot = 'http://localhost:9533/api/';
// let apiRoot = 'https://miniapp.tcyhw.top/v3/api/';
let api = {
	common: {
		aboutUs: apiRoot + 'aboutUs',
		verify: apiRoot + "verify",
	},
	article: {
		index: apiRoot + 'getIndex',
		category: apiRoot + 'getCategory',
		detail: apiRoot + 'detail',
		comment: apiRoot + 'comment',
		commentDetail: apiRoot + "commentDetail",
		commentReply: apiRoot + "commentReply",
		addComment: apiRoot + 'addComment',
		addReply: apiRoot + 'addReply',
		like: apiRoot + "like",
		commentLike: apiRoot + "commentLike",
		commentReplyLike: apiRoot + "commentReplyLike",
		favorite: apiRoot + "favorite",
		favoriteList: apiRoot + "favoriteList",
	},
	user: {
		login: apiRoot + "login",
		register: apiRoot + "register",
		index: apiRoot + "userIndex",
		userInfo: apiRoot + "userInfo",
		updateAvatar: apiRoot + "updateAvatar",
		upload: apiRoot + "upload",
		setting: apiRoot + "setting",
		updatePassword: apiRoot + "updatePassword",
		forgot: apiRoot + "forgot",
		updatePasswordByMobile: apiRoot + "updatePasswordByMobile",
		feedback: apiRoot + "feedback",
		logout: apiRoot + "logout",
	},
	wechat: {
		miniAppLogin: apiRoot + 'miniAppLogin',
		mpLogin: apiRoot + 'mpLogin',
		devLogin: apiRoot + 'devLogin',
		mpConfig: apiRoot + 'mpConfig',
	},
};
export default api;
