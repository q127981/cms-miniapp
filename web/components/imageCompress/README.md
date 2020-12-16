# uni-app 图片压缩插件、带图片自动旋转修正(暂时只支持H5)
### 作者：诗小柒
## 说明
uni-app 图片压缩插件、带图片自动旋转修正(暂时只支持H5)，支持APP 微信小程序 H5

### 使用方法
在 `script` 中引入组件
``` javascript
import imageCompress from "../../components/imageCompress/imageCompress.vue"
export default {
    components: {imageCompress}
}
```
在 `template` 中使用
``` javascript
<imageCompress ref="imageCompress" @result="imageCompressOk" @err="imageCompressErr" :number="2" :fixOrientation="true" :size="500" :maxWidth="1000" :ql="0.9" type="url"></imageCompress>
```

### 属性
|属性名|类型|默认值|可选值|说明|
|:-|:-:|:--:|:--:|-:|
|size|Number|500| |照片大小超过此值就压缩，且最大宽高等于maxWidth|
|maxWidth|Number|750| |照片宽高超过此值就压缩，且最大宽高等于此值|
|ql|Number|0.92|0-1 |照片压缩比|
|type|String|url|base64|照片压缩后返回的格式|
|fixOrientation|Boolean|true|false|是否修正图片方向（暂时只支持H5）|
|number|Number|1| |一次压缩图片数量（不支持H5）|


### 方法
|方法名|参数|默认值|说明|
|:-|:-:|:--:|-:|
|_changImg()| | |选择照片并开始压缩|

### 事件
|事件名|返回值|说明|
|:-|:-:|-:|
|result|Array数组，包含图片base64或临时地址|压缩成功的回调 并返回结果|
|err| |压缩失败的回调 并返回结果|

### 问题
1. H5平台,type 属性无效（返回的是base64或blob地址）
2. 可能无法压缩png
3. 图片自动旋转修正暂时只支持H5
4. 多选只支持App、小程序