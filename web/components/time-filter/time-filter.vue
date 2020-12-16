<template>
    <view class="dateContent">
        <view class="line"></view>
        <view  v-for="(item,key) in dateList" :class="{'item':true, 'item-checked': key==nowIndex, 'item-close': !item.isActive}" :key="key" @click="choseTime(item,key)">
            <!--<view class="value">{{item.value}}</view>-->
            <view class="circle"></view>
            <view class="week">{{item.name}}</view>
        </view>
    </view>
</template>

<script>
  export default {
    props: {
      type: {
        type: String,
        default: 'month'
      }
    },
    computed: {
      dateList() {
        var arr = this.getArr(this.type)
        return arr
      }
    },
    data() {
      return {
        nowIndex: 6
      }
    },
    methods: {
      getArr(type) {
        var timeArr = []
        if(type == 'month'){
          timeArr = [
            {
               name: '6月前',
              isActive: true,
              checked: false,
              value: -6
            },
            {
              name: '5月前',
              isActive: true,
              checked: false,
              value: -5
            },
            {
              name: '4月前',
              isActive: true,
              checked: false,
              value: -4
            },
            {
              name: '3月前',
              isActive: true,
              checked: false,
              value: -3
            },
            {
              name: '2月前',
              isActive: true,
              checked: false,
              value: -2
            },
            {
              name: '上月',
              isActive: true,
              checked: false,
              value: -1
            },
            {
              name: '本月',
              isActive: true,
              checked: true,
              value: 0
            }
          ]
        }
        if(type == 'day'){
          timeArr = [
            {
              name: '6天前',
              isActive: true,
              checked: false,
              value: -6
            },
            {
              name: '5天前',
              isActive: true,
              checked: false,
              value: -5
            },
            {
              name: '4天前',
              isActive: true,
              checked: false,
              value: -4
            },
            {
              name: '3天前',
              isActive: true,
              checked: false,
              value: -3
            },
            {
              name: '2天前',
              isActive: true,
              checked: false,
              value: -2
            },
            {
              name: '昨天',
              isActive: true,
              checked: false,
              value: -1
            },
            {
              name: '今天',
              isActive: true,
              checked: true,
              value: 0
            }
          ]
        }
        return timeArr;
      },
      getMonth(month) {
        var time = new Date();
        time.setMonth(time.getMonth() + month);//设置month月后的时间
        var y = time.getFullYear();
        var m = time.getMonth() + 1;//获取当前月份
        var d = time.getDate();
        if(m<10){
          m = "0"+m
        }
        return y + "-" + m ;
      },
      addDate(days) {
        var d = new Date();
        d.setDate(d.getDate() + days);
        var m = d.getMonth() + 1;
        if(m < 10){
          m='0'+m
        }
        var day = d.getDate()
        if(day < 10){
          day='0'+day
        }
        return d.getFullYear() + '-' + m + '-' + day;
      },
      choseTime(item, index) {
        if(this.type == 'month'){
          console.log(this.getMonth(item.value))
        }
        if(this.type == 'day'){
          console.log(this.addDate(item.value))
        }
        this.nowIndex = index
      }
    }
  }
</script>

<style lang="scss">
  .dateContent{
      background: $uni-bg-color;
      display: flex;
      flex-direction: row;
      padding: 18upx 6upx 4upx 6rpx;
      .item{
          z-index: 9;
          color: $uni-text-color-grey;
          flex: 1;
          text-align: center;
          padding-bottom: 12rpx;
          &-checked{
              color: #3FC0A2;
              font-weight: bolder;
              .circle{
                  background-color: #3FC0A2!important;
              }
          }
          &-close{
              color: $uni-text-color-grey;
          }
          .week{
              margin-top: 4rpx;
              font-size: $uni-font-size-base;
          }
          .circle{
              width: 18rpx;
              height: 18rpx;
              border-radius: 50%;
              border:1px solid #ebedf0;
              margin: 20upx auto ;
              background-color: $uni-bg-color;
          }
      }
      .line{
          position: absolute;
          top: 47rpx;
          width: 94%;
          left: 3%;
          height: 4rpx;
          background-color: #ebedf0;
      }
  }

</style>