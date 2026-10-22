<template>
  <div class="group-page">
    <!-- 发布入口 -->
    <section class="group-page__composer">
      <van-image round width="40px" height="40px" :src="myAvatar">
        <template v-slot:error>加载失败</template>
      </van-image>
      <button
        type="button"
        class="group-page__composer-input"
        @click="openComposer"
      >
        分享你的自习日常…
      </button>
      <van-button type="primary" size="small" round @click="openComposer"
        >发布</van-button
      >
    </section>

    <!-- 动态列表：不同用户发布差异化内容 -->
    <article
      class="group-page__post"
      :class="{ 'is-highlight': highlightId === post.id }"
      :id="`post-${post.id}`"
      v-for="post in posts"
      :key="post.id"
    >
      <div class="group-page__author">
        <van-image
          round
          width="48px"
          height="48px"
          :src="resolveAvatar(post.avatar, post.author)"
        >
          <template v-slot:error>加载失败</template>
          <template v-slot:loading>
            <van-loading type="spinner" size="20" />
          </template>
        </van-image>
        <div class="group-page__author-info">
          <div class="font-bold">{{ post.author }}</div>
          <div class="group-page__meta">
            <span class="--van-gray-6">{{ post.time }}</span>
            <span v-if="post.location" class="group-page__location">
              <van-icon name="location-o" size="12" />
              {{ post.location }}
            </span>
          </div>
        </div>
      </div>

      <div class="group-page__content">
        <div class="font-bold">{{ post.title }}</div>
        <div
          :class="{
            'van-multi-ellipsis--l3': !expandedPosts.has(post.id),
            'group-page__text': true
          }"
          :title="expandedPosts.has(post.id) ? '点击收起' : '点击展开'"
          @click="togglePost(post.id)"
        >
          {{ post.content }}
        </div>

        <div class="group-page__imgs" v-if="post.images.length">
          <van-image
            v-for="(img, index) in post.images"
            :key="index"
            width="112px"
            height="112px"
            radius="8"
            :src="img"
            class="group-page__img"
            @click="previewPostImages(post.images, index)"
          >
            <template v-slot:error>加载失败</template>
            <template v-slot:loading>
              <van-loading type="spinner" size="20" />
            </template>
          </van-image>
        </div>

        <div class="group-page__stats">
          <button
            type="button"
            class="group-page__stat"
            :class="{ 'is-active': post.liked }"
            @click="toggleLike(post)"
          >
            <van-icon
              :name="post.liked ? 'good-job' : 'good-job-o'"
              size="18"
            />
            {{ post.likes }}
          </button>
          <button
            type="button"
            class="group-page__stat"
            @click="togglePost(post.id)"
          >
            <van-icon name="comment-o" size="18" />
            {{ post.comments }}
          </button>
        </div>
      </div>
    </article>

    <!-- 发布弹窗 -->
    <van-dialog
      v-model:show="composerShow"
      title="发布动态"
      show-cancel-button
      confirm-button-text="发布"
      @confirm="submitPost"
    >
      <div class="group-page__composer-dialog">
        <van-field
          v-model="draftContent"
          type="textarea"
          rows="4"
          maxlength="200"
          show-word-limit
          autofocus
          placeholder="分享你的自习心得、好去处或者学习搭子…"
        />
      </div>
    </van-dialog>
  </div>
</template>

<script setup lang="ts" name="Group">
import { computed, nextTick, onMounted, reactive, ref } from "vue";
import { useRoute } from "vue-router";
import { showImagePreview, showToast } from "vant";
import { communityPosts, type CommunityPost } from "@/mock/community";
import { resolveAvatar } from "@/utils/avatar";
import { useUserStore } from "@/store/modules/user";
import avatarShangan from "@/assets/avatar-shangan.svg";

const route = useRoute();
const userStore = useUserStore();
const myAvatar = computed(
  () => userStore.userInfo?.avatarUrl || avatarShangan
);

// 拷贝一份，避免直接改动 mock 源数据
const posts = reactive<CommunityPost[]>(
  communityPosts.map(item => ({ ...item, images: [...item.images] }))
);

const expandedPosts = reactive(new Set<number>());
const togglePost = (id: number) =>
  expandedPosts.has(id) ? expandedPosts.delete(id) : expandedPosts.add(id);

// 从消息页跳转过来时，定位并高亮对应帖子
const highlightId = ref<number | null>(null);
onMounted(() => {
  const postId = Number(route.query.postId);
  if (!postId) return;
  highlightId.value = postId;
  nextTick(() => {
    document
      .getElementById(`post-${postId}`)
      ?.scrollIntoView({ behavior: "smooth", block: "start" });
  });
});

const toggleLike = (post: CommunityPost) => {
  post.liked = !post.liked;
  post.likes += post.liked ? 1 : -1;
};

const previewPostImages = (images: string[], index: number) => {
  showImagePreview({ images, startPosition: index });
};

// 发布动态
const composerShow = ref(false);
const draftContent = ref("");
const openComposer = () => {
  composerShow.value = true;
};

const formatNow = () => {
  const d = new Date();
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(
    d.getHours()
  )}:${pad(d.getMinutes())}`;
};

const submitPost = () => {
  const content = draftContent.value.trim();
  if (!content) {
    showToast("先写点内容再发布吧～");
    return;
  }
  posts.unshift({
    id: Date.now(),
    author: userStore.userInfo?.nickname || "我",
    avatar: userStore.userInfo?.avatarUrl,
    time: formatNow(),
    title: content.slice(0, 18),
    content,
    images: [],
    likes: 0,
    comments: 0,
    liked: false,
    location: "上岸自习室"
  });
  draftContent.value = "";
  showToast("发布成功");
};
</script>

<style lang="less" scoped>
.group-page {
  max-width: 760px;
  margin: 0 auto;
  padding: 28px 24px 0;
}

.group-page__composer {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding: 16px 20px;
  border-radius: 12px;
  background-color: var(--van-background-2);
  border: 1px solid var(--van-border-color);
}

.group-page__composer-input {
  flex: 1;
  min-width: 0;
  height: 38px;
  padding: 0 16px;
  text-align: left;
  border: 1px solid var(--van-border-color);
  border-radius: 19px;
  background: transparent;
  color: var(--van-text-color-3);
  font-size: 14px;
  cursor: pointer;
  transition: border-color 0.2s, background-color 0.2s;

  &:hover {
    border-color: var(--van-primary-color);
    background-color: var(--van-active-color);
  }

  &:focus-visible {
    outline: 2px solid var(--van-primary-color);
    outline-offset: 1px;
  }
}

.group-page__composer-dialog {
  padding: 8px 0 16px;
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

.group-page__post.is-highlight {
  border-color: var(--van-primary-color);
  box-shadow: 0 0 0 2px var(--van-primary-color);
  animation: group-post-highlight 1.8s ease;
}

@keyframes group-post-highlight {
  0% {
    background-color: var(--van-active-color);
  }
  100% {
    background-color: var(--van-background-2);
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
  gap: 2px;
  font-size: 13px;
}

.group-page__meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.group-page__location {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  color: var(--van-text-color-2);
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
  white-space: pre-wrap;
  word-break: break-word;
}

.group-page__imgs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.group-page__img {
  cursor: zoom-in;
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
  padding: 0;
  border: none;
  background: transparent;
  font-size: 13px;
  color: var(--van-text-color-2);
  cursor: pointer;
  transition: color 0.2s;

  &:hover {
    color: var(--van-primary-color);
  }

  &:focus-visible {
    outline: 2px solid var(--van-primary-color);
    outline-offset: 2px;
    border-radius: 4px;
  }

  &.is-active {
    color: var(--van-primary-color);
    font-weight: 600;
  }
}
</style>
