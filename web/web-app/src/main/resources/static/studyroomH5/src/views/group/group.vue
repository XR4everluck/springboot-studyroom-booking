<template>
  <div class="group-page">
    <article class="group-page__post" v-for="item in 5" :key="item">
      <div class="group-page__author">
        <van-image
          round
          width="48px"
          height="48px"
          src="https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg"
        >
          <template v-slot:error>加载失败</template>
          <template v-slot:loading>
            <van-loading type="spinner" size="20" />
          </template>
        </van-image>
        <div class="group-page__author-info">
          <div class="font-bold">游湖123456</div>
          <div class="--van-gray-6">2022-10-01 12:00:00</div>
        </div>
      </div>

      <div class="group-page__content">
        <div class="font-bold">分享楼下的一家咖啡厅</div>
        <div
          :class="{
            'van-multi-ellipsis--l3': !expandedPosts.has(item),
            'group-page__text': true
          }"
          :title="expandedPosts.has(item) ? '点击收起' : '点击展开'"
          @click="togglePost(item)"
        >
          噫吁嚱，危乎高哉！蜀道之难，难于上青天！蚕丛及鱼凫，开国何茫然！尔来四万八千岁，不与秦塞通人烟。西当太白有鸟道，可以横绝峨眉巅。地崩山摧壮士死，然后天梯石栈相钩连。上有六龙回日之高标，下有冲波逆折之回川。黄鹤之飞尚不得过，猿猱欲度愁攀援。青泥何盘盘，百步九折萦岩峦。扪参历井仰胁息，以手抚膺坐长叹。
          问君西游何时还？畏途巉岩不可攀。但见悲鸟号古木，雄飞雌从绕林间。又闻子规啼夜月，愁空山。蜀道之难，难于上青天，使人听此凋朱颜！连峰去天不盈尺，枯松倒挂倚绝壁。飞湍瀑流争喧豗，砯崖转石万壑雷。其险也如此，嗟尔远道之人胡为乎来哉！
          剑阁峥嵘而崔嵬，一夫当关，万夫莫开。所守或匪亲，化为狼与豺。朝避猛虎，夕避长蛇；磨牙吮血，杀人如麻。锦城虽云乐，不如早还家。蜀道之难，难于上青天，侧身西望长咨嗟！
        </div>

        <div class="group-page__imgs">
          <van-image
            v-for="n in 5"
            :key="n"
            width="112px"
            height="112px"
            radius="8"
            src="https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg"
          >
            <template v-slot:error>加载失败</template>
            <template v-slot:loading>
              <van-loading type="spinner" size="20" />
            </template>
          </van-image>
        </div>

        <div class="group-page__stats">
          <span class="group-page__stat">
            <van-icon name="good-job-o" size="18" />
            123
          </span>
          <span class="group-page__stat">
            <van-icon name="comment-o" size="18" />
            123
          </span>
        </div>
      </div>
    </article>
  </div>
</template>

<script setup lang="ts" name="Group">
import { reactive } from "vue";

const expandedPosts = reactive(new Set<number>());
const togglePost = (item: number) =>
  expandedPosts.has(item) ? expandedPosts.delete(item) : expandedPosts.add(item);
</script>
<style lang="less" scoped>
.group-page {
  max-width: 760px;
  margin: 0 auto;
  padding: 28px 24px 0;
}

.group-page__post {
  padding: 20px 24px;
  border-radius: 12px;
  background-color: var(--van-background-2);
  border: 1px solid var(--van-border-color);

  & + & {
    margin-top: 16px;
  }
}

.group-page__author {
  display: flex;
  align-items: center;
  gap: 10px;
}

.group-page__author-info {
  display: flex;
  flex-direction: column;
  font-size: 13px;
}

.group-page__content {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 14px;
}

.group-page__text {
  cursor: pointer;
  line-height: 1.7;
  color: var(--van-text-color-2);
}

.group-page__imgs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.group-page__stats {
  display: flex;
  gap: 24px;
  padding-top: 4px;
}

.group-page__stat {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--van-text-color-2);
  cursor: pointer;

  &:hover {
    color: var(--van-primary-color);
  }
}
</style>
