/**
 * 生成「昵称首字 + 主题渐变」的兜底头像（data-URI SVG）。
 * 用于没有真实头像的用户，避免加载失败露出破图。
 */
const PALETTE = [
  "#276749",
  "#2b6cb0",
  "#b7791f",
  "#6b46c1",
  "#c53030",
  "#2c7a7b",
  "#0f7b6c",
  "#975a16"
];

export function buildLetterAvatar(name?: string, size = 96): string {
  const label = (name || "?").trim();
  const char = label.charAt(0).toUpperCase() || "?";
  let hash = 0;
  for (let i = 0; i < label.length; i++) {
    hash = (hash * 31 + label.charCodeAt(i)) >>> 0;
  }
  const color = PALETTE[hash % PALETTE.length];

  const svg =
    `<svg xmlns="http://www.w3.org/2000/svg" width="${size}" height="${size}" viewBox="0 0 100 100">` +
    `<defs><linearGradient id="g" x1="0" y1="0" x2="1" y2="1">` +
    `<stop offset="0%" stop-color="${color}" stop-opacity="0.82"/>` +
    `<stop offset="100%" stop-color="${color}"/>` +
    `</linearGradient></defs>` +
    `<rect width="100" height="100" rx="50" fill="url(#g)"/>` +
    `<text x="50" y="52" text-anchor="middle" dominant-baseline="middle" ` +
    `font-family="Microsoft YaHei,PingFang SC,SimHei,sans-serif" font-size="44" ` +
    `font-weight="600" fill="#ffffff">${char}</text>` +
    `</svg>`;

  return `data:image/svg+xml;charset=utf-8,${encodeURIComponent(svg)}`;
}

/** 头像兜底：有真实头像用真实头像，否则用首字头像 */
export function resolveAvatar(avatar: string | undefined, name?: string): string {
  return avatar || buildLetterAvatar(name);
}
