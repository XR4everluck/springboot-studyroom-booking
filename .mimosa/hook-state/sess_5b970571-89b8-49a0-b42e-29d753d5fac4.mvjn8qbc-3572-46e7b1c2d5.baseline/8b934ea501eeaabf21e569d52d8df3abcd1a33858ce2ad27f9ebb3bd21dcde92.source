import coffeeImg from "@/assets/posts/coffee.svg";
import deskImg from "@/assets/posts/desk.svg";
import stationeryImg from "@/assets/posts/stationery.svg";
import notesImg from "@/assets/posts/notes.svg";
import libraryImg from "@/assets/posts/library.svg";
import teamImg from "@/assets/posts/team.svg";

/** 圈子动态 */
export interface CommunityPost {
  /** 动态 id */
  id: number;
  /** 发布者昵称 */
  author: string;
  /** 发布者头像，缺省时按昵称生成首字头像 */
  avatar?: string;
  /** 发布时间 */
  time: string;
  /** 标题 */
  title: string;
  /** 正文 */
  content: string;
  /** 配图 */
  images: string[];
  /** 点赞数 */
  likes: number;
  /** 评论数 */
  comments: number;
  /** 当前用户是否已点赞 */
  liked: boolean;
  /** 位置标签 */
  location?: string;
}

/** 消息类型：通知 / 评论 / 赞 */
export type MessageType = "notice" | "comment" | "like";

/** 消息中心条目 */
export interface CommunityMessage {
  id: number;
  type: MessageType;
  /** 触发者昵称（通知类为发送方名称） */
  nickname: string;
  /** 触发者头像，缺省时按昵称生成首字头像 */
  avatar?: string;
  /** 时间描述 */
  time: string;
  /** 列表摘要 */
  summary: string;
  /** 详情全文 */
  content: string;
  /** 关联的动态 id（评论 / 赞类可用，用于跳转到圈子对应帖子） */
  postId?: number;
  /** 关联动态标题 */
  postTitle?: string;
  /** 通知类的跳转路径 */
  link?: string;
  /** 跳转按钮文案 */
  linkText?: string;
  /** 是否已读 */
  read: boolean;
}

export const postImage = {
  coffee: coffeeImg,
  desk: deskImg,
  stationery: stationeryImg,
  notes: notesImg,
  library: libraryImg,
  team: teamImg
};

/** 圈子动态：不同用户发布差异化内容 */
export const communityPosts: CommunityPost[] = [
  {
    id: 1,
    author: "上岸的鱼",
    time: "10-13 09:12",
    title: "分享楼下的一家咖啡厅",
    content:
      "背书背到脑壳疼，出来透口气，结果发现了这家新开的咖啡厅。落地窗采光特别好，靠窗的位置能看到一整条梧桐街。点了一杯燕麦拿铁，坐了俩小时刷完两套真题，效率出奇地高。分享给还在找自习据点的同学～",
    images: [coffeeImg, deskImg],
    likes: 128,
    comments: 36,
    liked: false,
    location: "武汉 · 珞喻路"
  },
  {
    id: 2,
    author: "考研小张",
    time: "10-12 20:45",
    title: "求组队！周六上午图书馆四楼",
    content:
      "本人二战，数学是短板，想找一两个同学周六上午一起去省图四楼自习。互相监督打卡，中途还能交换一下错题本。有意向的评论区吱一声，我们拉个小群～",
    images: [libraryImg],
    likes: 42,
    comments: 18,
    liked: false,
    location: "湖北省图书馆"
  },
  {
    id: 3,
    author: "图书馆钉子户",
    time: "10-12 07:30",
    title: "坚持早八自习第 30 天打卡",
    content:
      "从开学到现在每天 7:30 到自习室，已经连续 30 天没有断过。最开始靠意志力硬扛，现在慢慢成了习惯。早上那段安静的时间，真的是一天里最清醒、最出效率的时候。给还在跟被窝作斗争的同学打打气！",
    images: [deskImg],
    likes: 266,
    comments: 54,
    liked: true,
    location: "静悦自习室（南山店）"
  },
  {
    id: 4,
    author: "夜猫子阿哲",
    time: "10-11 23:58",
    title: "有没有晚上一起自习的朋友",
    content:
      "白天上班，只能晚上来。21:00 之后的自习室人少灯暖，特别适合刷题。就是一个人有点难坚持，想找同城的搭子互相提醒别摸鱼。评论区蹲一个～",
    images: [],
    likes: 19,
    comments: 7,
    liked: false
  },
  {
    id: 5,
    author: "元气少女",
    time: "10-11 15:20",
    title: "安利一个超好用的番茄钟",
    content:
      "用了一个月的番茄工作法：25 分钟专注 + 5 分钟休息，一天下来能稳定学 8 个番茄，比从前“假装学习一整天”实在太多了。计时器放在桌角，手机直接塞进包里，亲测有效，强烈推荐！",
    images: [stationeryImg, notesImg],
    likes: 97,
    comments: 23,
    liked: false
  },
  {
    id: 6,
    author: "三战老哥",
    time: "10-10 21:05",
    title: "分享一下我整理的冲刺时间表",
    content:
      "距离考试还有一个多月，把每天切成上午背诵、下午刷题、晚上复盘三块，每块之间留 20 分钟放空。坚持两周后专注度明显回来了。时间表我拍了照放在下面，需要的自取，祝大家都上岸！",
    images: [notesImg, deskImg, libraryImg],
    likes: 341,
    comments: 88,
    liked: false,
    location: "上岸自习室（光谷店）"
  },
  {
    id: 7,
    author: "一起上岸吧",
    time: "10-10 12:40",
    title: "自习室拼单群招新",
    content:
      "最近会员卡有活动，凑满 5 人拼单更划算。群里现在 3 个人，还差两位，想长期固定来自习的同学可以私信我，晚上 20:00 统一拉群。",
    images: [teamImg],
    likes: 33,
    comments: 11,
    liked: false,
    location: "上岸自习室（光谷店）"
  }
];

/** 消息中心：通知 / 评论 / 赞三类差异化数据 */
export const communityMessages: CommunityMessage[] = [
  // 通知
  {
    id: 101,
    type: "notice",
    nickname: "预订提醒",
    time: "今天 12:30",
    summary: "你预订的『静悦自习室（南山店）· A12 座』将于今天 18:00 开始",
    content:
      "你在「静悦自习室（南山店）」预订的 A12 座位将于今天 18:00 开始使用，请提前 10 分钟到店，出示订单信息完成签到。若无法到店，请在开始前取消订单以免占用座位。",
    link: "/myOrder",
    linkText: "查看我的订单",
    read: false
  },
  {
    id: 102,
    type: "notice",
    nickname: "上岸自习室",
    time: "今天 09:00",
    summary: "本周会员日：充值满 100 送 20，会员卡抵扣更划算",
    content:
      "本周会员日福利来了！充值满 100 元赠送 20 元余额，会员卡下单可直接抵扣房费。活动截止本周日 24:00，快来个人中心查看你的会员卡吧。",
    link: "/userCenter",
    linkText: "前往个人中心",
    read: true
  },
  {
    id: 103,
    type: "notice",
    nickname: "签退成功",
    time: "昨天 21:05",
    summary: "订单 SO20261012001 已签退，本次使用 2.5 小时",
    content:
      "你的订单 SO20261012001 已成功签退，本次使用时长 2.5 小时，座位已释放。欢迎再次光临上岸自习室，祝你复习顺利、早日上岸！",
    link: "/myOrder",
    linkText: "查看订单详情",
    read: true
  },
  // 评论
  {
    id: 201,
    type: "comment",
    nickname: "上岸的鱼",
    time: "今天 10:20",
    summary: "评论了你的动态「坚持早八自习第 30 天打卡」",
    content: "太自律了！我最近也在努力早起，方便的话求带带，一起打卡呀～",
    postId: 3,
    postTitle: "坚持早八自习第 30 天打卡",
    read: false
  },
  {
    id: 202,
    type: "comment",
    nickname: "考研小张",
    time: "昨天 20:52",
    summary: "回复了你的评论：「四楼靠窗的位置今天很空」",
    content: "四楼靠窗的位置今天特别空，我已经占好座了，你要来的话我帮你留一个。",
    postId: 2,
    postTitle: "求组队！周六上午图书馆四楼",
    read: false
  },
  {
    id: 203,
    type: "comment",
    nickname: "元气少女",
    time: "昨天 16:08",
    summary: "评论了你的动态「分享一下我整理的冲刺时间表」",
    content: "表格太清晰啦，已经收藏！跟着这个节奏试了一周，专注度真的回来了，谢谢分享～",
    postId: 6,
    postTitle: "分享一下我整理的冲刺时间表",
    read: false
  },
  {
    id: 204,
    type: "comment",
    nickname: "夜猫子阿哲",
    time: "10-11 23:59",
    summary: "评论了你的动态「有没有晚上一起自习的朋友」",
    content: "同城！我也常来晚上这一场，加个好友一起呗，互相监督不摸鱼。",
    postId: 4,
    postTitle: "有没有晚上一起自习的朋友",
    read: true
  },
  // 赞
  {
    id: 301,
    type: "like",
    nickname: "图书馆钉子户",
    time: "今天 11:02",
    summary: "赞了你的动态「分享楼下的一家咖啡厅」",
    content: "你的动态「分享楼下的一家咖啡厅」收到了一个新的赞。",
    postId: 1,
    postTitle: "分享楼下的一家咖啡厅",
    read: false
  },
  {
    id: 302,
    type: "like",
    nickname: "上岸的鱼 等 12 人",
    time: "昨天 22:31",
    summary: "赞了你的动态「坚持早八自习第 30 天打卡」",
    content: "你的动态「坚持早八自习第 30 天打卡」共收到 12 个赞，继续加油！",
    postId: 3,
    postTitle: "坚持早八自习第 30 天打卡",
    read: false
  },
  {
    id: 303,
    type: "like",
    nickname: "三战老哥",
    time: "昨天 19:14",
    summary: "赞了你的评论「说得好，坚持就是胜利」",
    content: "你发表的评论「说得好，坚持就是胜利」收到了一个赞。",
    postId: 6,
    postTitle: "分享一下我整理的冲刺时间表",
    read: true
  },
  {
    id: 304,
    type: "like",
    nickname: "元气少女",
    time: "10-11 18:47",
    summary: "赞了你的动态「安利一个超好用的番茄钟」",
    content: "你的动态「安利一个超好用的番茄钟」收到一个新的赞。",
    postId: 5,
    postTitle: "安利一个超好用的番茄钟",
    read: true
  }
];
