<template>
  <div class="message-page">
    <!-- 三个消息入口：可点击切换 -->
    <div class="message-page__cats">
      <button
        v-for="cat in navList"
        :key="cat.type"
        type="button"
        class="message-page__cat"
        :class="{ 'is-active': activeType === cat.type }"
        :style="{ background: cat.color }"
        @click="switchType(cat.type)"
      >
        <van-icon
          :name="cat.icon"
          :badge="unreadCount(cat.type) || ''"
          size="25"
        />
        <span class="message-page__cat-name">{{ cat.name }}</span>
      </button>
    </div>

    <!-- 工具条：数量 + 全部已读 -->
    <div class="message-page__toolbar">
      <span class="--van-gray-6">
        {{ currentCatName }} · 共 {{ currentList.length }} 条
      </span>
      <van-button
        size="small"
        plain
        type="primary"
        :disabled="!hasUnread"
        @click="markAllRead"
        >全部已读</van-button
      >
    </div>

    <!-- 消息列表 -->
    <div class="message-page__list" v-if="currentList.length">
      <div
        class="message-page__item"
        :class="{ 'is-unread': !item.read }"
        v-for="item in currentList"
        :key="item.id"
        @click="openMessage(item)"
      >
        <van-image
          round
          width="56px"
          height="56px"
          :src="resolveAvatar(item.avatar, item.nickname)"
        >
          <template v-slot:error>加载失败</template>
          <template v-slot:loading>
            <van-loading type="spinner" size="20" />
          </template>
        </van-image>
        <div class="message-page__body">
          <div class="message-page__head">
            <div class="font-bold">{{ item.nickname }}</div>
            <div class="--van-gray-6">{{ item.time }}</div>
          </div>
          <div class="--van-gray-6 van-ellipsis message-page__summary">
            {{ item.summary }}
          </div>
        </div>
        <span v-if="!item.read" class="message-page__dot" />
      </div>
    </div>
    <van-empty v-else :description="`暂无${currentCatName}`" />
  </div>

  <!-- 消息详情 -->
  <van-dialog
    v-model:show="detailShow"
    :title="detailTitle"
    :show-confirm-button="Boolean(detailJump)"
    :confirm-button-text="detailJumpText"
    show-cancel-button
    cancel-button-text="关闭"
    @confirm="onDetailConfirm"
  >
    <div class="message-page__detail" v-if="detail">
      <div class="message-page__detail-meta">
        <span class="font-bold">{{ detail.nickname }}</span>
        <span class="--van-gray-6">{{ detail.time }}</span>
      </div>
      <p v-if="detail.postTitle" class="message-page__detail-ref">
        关联动态：{{ detail.postTitle }}
      </p>
      <p class="message-page__detail-content">{{ detail.content }}</p>
    </div>
  </van-dialog>
</template>

<script setup lang="ts" name="Message">
import { computed, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { showToast } from "vant";
import {
  communityMessages,
  type CommunityMessage,
  type MessageType
} from "@/mock/community";
import { resolveAvatar } from "@/utils/avatar";

const router = useRouter();

const navList = ref([
  { type: "notice" as MessageType, icon: "volume-o", name: "通知", color: "#f1be4a" },
  { type: "comment" as MessageType, icon: "chat-o", name: "评论", color: "#51b290" },
  { type: "like" as MessageType, icon: "good-job-o", name: "赞", color: "#f39653" }
]);

const typeNameMap: Record<MessageType, string> = {
  notice: "通知",
  comment: "评论",
  like: "赞"
};

const activeType = ref<MessageType>("notice");
const messages = reactive<CommunityMessage[]>(
  communityMessages.map(item => ({ ...item }))
);

const switchType = (type: MessageType) => {
  activeType.value = type;
};

const currentList = computed(() =>
  messages.filter(item => item.type === activeType.value)
);
const currentCatName = computed(() => typeNameMap[activeType.value]);

const unreadCount = (type: MessageType) =>
  messages.filter(item => item.type === type && !item.read).length;
const hasUnread = computed(() => messages.some(item => !item.read));

const markAllRead = () => {
  messages.forEach(item => (item.read = true));
  showToast("已全部标为已读");
};

// 详情弹窗
const detailShow = ref(false);
const detail = ref<CommunityMessage | null>(null);

const openMessage = (item: CommunityMessage) => {
  item.read = true;
  detail.value = item;
  detailShow.value = true;
};

const detailTitle = computed(() =>
  detail.value ? `${typeNameMap[detail.value.type]}详情` : "消息详情"
);

// 评论 / 赞：跳转到圈子对应帖子；通知：跳转到对应功能页
const detailJump = computed(() => {
  if (!detail.value) return null;
  if (detail.value.postId) {
    return { path: "/group", query: { postId: String(detail.value.postId) } };
  }
  if (detail.value.link) return { path: detail.value.link };
  return null;
});

const detailJumpText = computed(() => {
  if (!detail.value) return "查看";
  if (detail.value.postId) return "查看原帖";
  return detail.value.linkText || "前往查看";
});

const onDetailConfirm = () => {
  if (detailJump.value) router.push(detailJump.value);
};
</script>

<style lang="less" scoped>
.message-page {
  max-width: 760px;
  margin: 0 auto;
  padding: 28px 24px 0;
}

.message-page__cats {
  display: flex;
  justify-content: center;
  gap: 40px;
  padding: 20px;
  border-radius: 12px;
  background-color: var(--van-background-2);
  border: 1px solid var(--van-border-color);
}

.message-page__cat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  width: 88px;
  padding: 14px 0;
  border: none;
  border-radius: 12px;
  color: #fff;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s, filter 0.2s;
  filter: saturate(0.72) brightness(1.02);

  &:hover {
    transform: translateY(-2px);
    filter: saturate(1);
  }

  &:focus-visible {
    outline: 2px solid var(--van-primary-color);
    outline-offset: 2px;
  }

  &.is-active {
    filter: saturate(1);
    box-shadow: 0 6px 18px rgba(0, 0, 0, 0.16);
    transform: translateY(-2px);
  }
}

.message-page__cat-name {
  font-size: 13px;
}

.message-page__toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 16px 4px 8px;
  font-size: 13px;
}

.message-page__list {
  border-radius: 12px;
  background-color: var(--van-background-2);
  border: 1px solid var(--van-border-color);
  overflow: hidden;
}

.message-page__item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 18px 24px;
  cursor: pointer;
  transition: background-color 0.15s;

  &:hover {
    background-color: var(--van-active-color);
  }

  & + & {
    border-top: 1px solid var(--van-border-color);
  }
}

.message-page__body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 4px;
  font-size: 13px;
}

.message-page__head {
  display: flex;
  justify-content: space-between;
}

.message-page__summary {
  color: var(--van-text-color-2);
}

.message-page__dot {
  flex-shrink: 0;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: var(--van-danger-color, #ee0a24);
}

.message-page__detail {
  padding: 16px 4px 4px;
  font-size: 14px;
  line-height: 1.7;
  color: var(--van-text-color);
}

.message-page__detail-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.message-page__detail-ref {
  margin: 0 0 8px;
  padding: 8px 12px;
  border-radius: 8px;
  background-color: var(--van-active-color);
  color: var(--van-text-color-2);
  font-size: 13px;
}

.message-page__detail-content {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
