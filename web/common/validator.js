/**
 * 弹出框
 */
const toast = function(title) {
	uni.showToast({
		icon: 'none',
		title: title
	})
}

/**
 * 手机验证
 */
const checkMobile = function(value) {
	let reg = /^1\d{10}$/
	if (value == '') {
		toast('请输入手机号码');
		return false;
	} else if (!reg.test(value)) {
		toast('手机号码格式不正确');
		return false;
	}
	return true;
}

/**
 * 密码验证
 */
const checkNickname = function(value) {
	if (value == '') {
		toast('请输入昵称');
		return false;
	} else if (value.length < 2) {
		toast('昵称太短了');
		return false;
	} else if (value.length > 32) {
		toast('昵称太长了');
		return false;
	}
	return true;
}

/**
 * 密码验证
 */
const checkPassword = function(value) {
	if (value == '') {
		toast('请输入密码');
		return false;
	} else if (value.length < 6) {
		toast('密码长度不能小于6位');
		return false;
	}
	return true;
}

/**
 * 密码验证
 */
const checkNewPassword = function(value) {
	if (value == '') {
		toast('请输入密新码');
		return false;
	} else if (value.length < 6) {
		toast('新密码长度不能小于6位');
		return false;
	}
	return true;
}

/**
 * 验证码验证
 */
const checkVerifyCode = function(value) {
	let reg = /^\d{4}$/
	if (value == '') {
		toast('请输入验证码');
		return false;
	} else if (!reg.test(value)) {
		toast('验证码不正确');
		return false;
	}
	return true;
}

/**
 * 身份证验证
 */
const checkIdCard = function(value) {
	let reg = /^\d{15}|\d{18}$/
	if (value == '') {
		toast('请输入身份证号码');
		return false;
	} else if (!reg.test(value)) {
		toast('身份证必须是15或18位数字');
		return false;
	}
	return true;
}

/**
 * 银行卡验证
 */
const checkBankCard = function(value) {
	let reg = /^([1-9]{1})(\d{14}|\d{15}|\d{16}|\d{18})$/
	if (value == '') {
		toast('请输入银行卡号');
		return false;
	} else if (!reg.test(value)) {
		toast('银行卡号格式不正确');
		return false;
	}
	return true;
}

export default {
	checkMobile,
	checkNickname,
	checkPassword,
	checkNewPassword,
	checkVerifyCode,
	checkIdCard,
	checkBankCard
};
