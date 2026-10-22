<template>
  <aside class="filter-panel">
    <section class="filter-panel__group">
      <h4 class="filter-panel__title">地区</h4>
      <div class="filter-panel__chips">
        <button
          type="button"
          class="filter-panel__chip"
          :class="{ 'is-checked': item.isCheck }"
          @click="clickAreaItemHandle(item, 0)"
          v-for="item in areaInfo.provinceList"
          :key="item.id"
        >
          {{ item.name }}
        </button>
        <span v-if="!areaInfo.provinceList.length" class="filter-panel__empty"
          >暂无数据</span
        >
      </div>
      <div
        class="filter-panel__chips filter-panel__chips--sub"
        v-if="areaInfo.cityList.length"
      >
        <button
          type="button"
          class="filter-panel__chip"
          :class="{ 'is-checked': item.isCheck }"
          @click="clickAreaItemHandle(item, 1)"
          v-for="item in areaInfo.cityList"
          :key="item.id"
        >
          {{ item.name }}
        </button>
      </div>
      <div
        class="filter-panel__chips filter-panel__chips--sub"
        v-if="areaInfo.districtList.length"
      >
        <button
          type="button"
          class="filter-panel__chip"
          :class="{ 'is-checked': item.isCheck }"
          @click="clickAreaItemHandle(item, 2)"
          v-for="item in areaInfo.districtList"
          :key="item.id"
        >
          {{ item.name }}
        </button>
      </div>
    </section>

    <section class="filter-panel__group">
      <h4 class="filter-panel__title">价格</h4>
      <div class="filter-panel__chips">
        <button
          type="button"
          class="filter-panel__chip"
          :class="{ 'is-checked': item.isCheck }"
          @click="priceClickHandle(item)"
          v-for="item in priceInfoList"
          :key="item.label"
        >
          {{ item.label }}
        </button>
      </div>
    </section>

    <section class="filter-panel__group">
      <h4 class="filter-panel__title">付款方式</h4>
      <div class="filter-panel__chips">
        <button
          type="button"
          class="filter-panel__chip"
          :class="{ 'is-checked': item.isCheck }"
          @click="paymentClickHandle(item)"
          v-for="item in paymentInfoList"
          :key="item.label"
        >
          {{ item.label }}
        </button>
        <span v-if="!paymentInfoList.length" class="filter-panel__empty"
          >暂无数据</span
        >
      </div>
    </section>

    <section class="filter-panel__group">
      <h4 class="filter-panel__title">排序</h4>
      <div class="filter-panel__chips">
        <button
          type="button"
          class="filter-panel__chip"
          :class="{ 'is-checked': item.isCheck }"
          @click="orderTypeClickHandle(item)"
          v-for="item in orderTypeInfoList"
          :key="item.label"
        >
          {{ item.label }}
        </button>
      </div>
    </section>

    <div class="filter-panel__actions">
      <van-button size="small" @click="onResetHandle">重置</van-button>
      <van-button size="small" type="primary" @click="onConfirmHandle"
        >查找</van-button
      >
    </div>
  </aside>
</template>
<script setup lang="ts">
import { onMounted, ref } from "vue";
import type {
  LabelValueInterface,
  RegionInterface,
  SeatListQueryInterface
} from "@/api/search/types";
import {
  getCityList,
  getDistrictList,
  getPaymentTypeList,
  getProvinceList
} from "@/api/search";
import { SearchOrderType, SearchOrderTypeMap } from "@/enums/constEnums";
const props = defineProps({
  confirmCallback: {
    type: Function,
    default: () => ({})
  }
});
const formData = ref<Omit<SeatListQueryInterface, "current" | "size">>({
  // 省市区
  provinceId: "",
  cityId: "",
  districtId: "",
  minPrice: "",
  //   最大单价
  maxPrice: "",
  //   支付方式
  paymentTypeId: "",
  //   	价格排序方式,可用值:desc,asc
  orderType: ""
});

//#region <省市区查询相关>
const areaInfo = ref({
  // 省份数据
  provinceList: [] as RegionInterface[],
  // 市区数据
  cityList: [] as RegionInterface[],
  // 缓存旧数据
  oldCityListInfo: {
    provinceId: "",
    cityList: [] as RegionInterface[]
  },
  // 区县数据
  districtList: [] as RegionInterface[],
  //   缓存旧数据
  oldDistrictList: {
    cityId: "",
    districtList: [] as RegionInterface[]
  }
});
// 点击省市区
const clickAreaItemHandle = (item: RegionInterface, type: number) => {
  // type 0省 1市 2区
  if (type === 0) {
    if (item.isCheck) {
      item.isCheck = !item.isCheck;
      formData.value.provinceId = "";
      formData.value.cityId = "";
      formData.value.districtId = "";
      areaInfo.value.cityList.forEach(item2 => {
        item2.isCheck = false;
      });
      areaInfo.value.districtList.forEach(item2 => {
        item2.isCheck = false;
      });
      return;
    }
    areaInfo.value.provinceList.forEach(item2 => {
      item2.isCheck = false;
    });
    item.isCheck = !item.isCheck;
    formData.value.provinceId = item.id;
    provinceChangeCallback();
    //   设置市区列表
  } else if (type === 1) {
    if (item.isCheck) {
      item.isCheck = !item.isCheck;
      formData.value.cityId = "";
      formData.value.districtId = "";
      areaInfo.value.districtList.forEach(item2 => {
        item2.isCheck = false;
      });
      return;
    }
    areaInfo.value.cityList.forEach(item2 => {
      item2.isCheck = false;
    });
    item.isCheck = !item.isCheck;
    formData.value.cityId = item.id;
    cityChangeCallback();
  } else if (type === 2) {
    areaInfo.value.districtList.forEach(item2 => {
      if (item2.id !== item.id) {
        item2.isCheck = false;
      } else {
        item.isCheck = !item.isCheck;
      }
    });
    formData.value.districtId = item.isCheck ? item.id : "";
  }
};

// 获取省份
async function getProvinceListHandle() {
  try {
    const { data } = await getProvinceList();
    areaInfo.value.provinceList = data;
  } catch (error) {
    console.log(error);
  }
}

// 获取城市
async function getCityListHandle(
  provinceId: number | string = formData.value.provinceId
) {
  if (!provinceId) return;
  try {
    // 如果存在缓存的旧数据,使用旧数据
    if (
      areaInfo.value.oldCityListInfo.provinceId === provinceId &&
      areaInfo.value.oldCityListInfo.cityList.length
    ) {
      areaInfo.value.cityList = JSON.parse(
        JSON.stringify(areaInfo.value.oldCityListInfo.cityList)
      );
      return;
    }
    const { data } = await getCityList(provinceId);
    // 缓存旧数据
    areaInfo.value.oldCityListInfo.provinceId = provinceId as string;
    areaInfo.value.oldCityListInfo.cityList = JSON.parse(JSON.stringify(data));
    areaInfo.value.cityList = data;
  } catch (error) {
    console.log(error);
  }
}

// 获取区域
async function getDistrictListHandle(
  cityId: number | string = formData.value.cityId
) {
  if (!cityId) return;
  try {
    // 如果存在缓存的旧数据,使用旧数据
    if (
      areaInfo.value.oldDistrictList.cityId === cityId &&
      areaInfo.value.oldDistrictList.districtList.length
    ) {
      areaInfo.value.districtList = JSON.parse(
        JSON.stringify(areaInfo.value.oldDistrictList.districtList)
      );
      return;
    }
    const { data } = await getDistrictList(cityId);
    // 缓存旧数据
    areaInfo.value.oldDistrictList.cityId = cityId as string;
    areaInfo.value.oldDistrictList.districtList = JSON.parse(
      JSON.stringify(data)
    );
    areaInfo.value.districtList = data;
  } catch (error) {
    console.log(error);
  }
}

// 重置市数据
function resetCity() {
  formData.value.cityId = "";
  areaInfo.value.cityList = [];
}

// 重置区数据
function resetDistrict() {
  formData.value.districtId = "";
  areaInfo.value.districtList = [];
}

// 省份改变回调
const provinceChangeCallback = async () => {
  let provinceId = formData.value.provinceId;
  if (provinceId) {
    resetCity();
    resetDistrict();
    await getCityListHandle(provinceId);
  }
};
// 城市改变回调
const cityChangeCallback = async () => {
  let cityId = formData.value.cityId;
  if (cityId) {
    resetDistrict();
    await getDistrictListHandle(cityId);
  }
};

//#endregion
//#region <价格>
// 价格数据
const priceInfoList = ref<LabelValueInterface<(number | string)[]>[]>([
  {
    label: "不限",
    value: ["", ""],
    isCheck: true
  },
  {
    label: "0-1500元",
    value: [0, 1500],
    isCheck: false
  },
  {
    label: "1500-3000元",
    value: [1500, 3000],
    isCheck: false
  },
  {
    label: "3000-6000元",
    value: [3000, 6000],
    isCheck: false
  },
  {
    label: "6000-9000元",
    value: [6000, 9000],
    isCheck: false
  },

  {
    label: "9000-12000元",
    value: [9000, 12000],
    isCheck: false
  }
]);

// 价格点击
function priceClickHandle(item: LabelValueInterface<(number | string)[]>) {
  priceInfoList.value.forEach(item2 => {
    if (item.label === item2.label) {
      item.isCheck = !item.isCheck;
      formData.value.minPrice = item.isCheck ? item.value[0] : "";
      formData.value.maxPrice = item.isCheck ? item.value[1] : "";
    } else {
      item2.isCheck = false;
    }
  });
}

//#endregion
//#region <付款方式>
// 价格数据
const paymentInfoList = ref<LabelValueInterface[]>([]);

// 获取支付方式列表
async function getPaymentListHandle() {
  try {
    const { data } = await getPaymentTypeList();

    paymentInfoList.value =
      data?.map(item => ({
        label: item.name,
        value: item.id,
        isCheck: false
      })) || [];
  } catch (error) {
    console.log(error);
  }
}

// 价格点击
function paymentClickHandle(item: LabelValueInterface) {
  paymentInfoList.value.forEach(item2 => {
    if (item.label === item2.label) {
      item.isCheck = !item.isCheck;
      formData.value.paymentTypeId = item.isCheck ? item.value : "";
    } else {
      item2.isCheck = false;
    }
  });
}

//#endregion
//#region <排序>
// 价格数据
const orderTypeInfoList = ref<LabelValueInterface[]>(
  SearchOrderTypeMap.map(item => ({
    ...item,
    isCheck: false
  }))
);
// 价格点击
function orderTypeClickHandle(item: LabelValueInterface) {
  orderTypeInfoList.value.forEach(item2 => {
    if (item.label === item2.label) {
      item.isCheck = !item.isCheck;
      formData.value.orderType = item.isCheck
        ? (item.value as SearchOrderType)
        : "";
    } else {
      item2.isCheck = false;
    }
  });
}

//#endregion
const onConfirmHandle = () => {
  props.confirmCallback && props.confirmCallback(formData.value);
};
// 重置全部筛选条件并立即查询
const onResetHandle = () => {
  formData.value = {
    provinceId: "",
    cityId: "",
    districtId: "",
    minPrice: "",
    maxPrice: "",
    paymentTypeId: "",
    orderType: ""
  };
  areaInfo.value.provinceList.forEach(item => (item.isCheck = false));
  areaInfo.value.cityList = [];
  areaInfo.value.districtList = [];
  priceInfoList.value.forEach((item, index) => (item.isCheck = index === 0));
  paymentInfoList.value.forEach(item => (item.isCheck = false));
  orderTypeInfoList.value.forEach(item => (item.isCheck = false));
  onConfirmHandle();
};
onMounted(() => {
  // 获取省份列表
  getProvinceListHandle();
  // 获取支付方式列表
  getPaymentListHandle();
});
</script>

<style scoped lang="less">
.filter-panel {
  box-sizing: border-box;
  width: 240px;
  flex-shrink: 0;
  padding: 16px;
  background-color: var(--van-background-2);
  border: 1px solid var(--van-border-color);
  border-radius: 10px;
  align-self: flex-start;
}

.filter-panel__group {
  margin-bottom: 18px;
}

.filter-panel__title {
  margin: 0 0 8px;
  font-size: 13px;
  font-weight: 600;
  color: var(--van-text-color);
}

.filter-panel__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.filter-panel__chips--sub {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px dashed var(--van-border-color);
}

.filter-panel__chip {
  min-height: 32px;
  box-sizing: border-box;
  padding: 5px 13px;
  border: 1px solid var(--van-border-color);
  border-radius: 14px;
  background: transparent;
  color: var(--van-text-color-2);
  font-size: 12px;
  line-height: 1.4;
  cursor: pointer;
  transition: all 0.15s;

  &:hover {
    color: var(--van-primary-color);
    border-color: var(--van-primary-color);
  }

  &:focus-visible {
    outline: 2px solid var(--van-primary-color);
    outline-offset: 1px;
  }

  &.is-checked {
    background-color: var(--van-primary-color);
    border-color: var(--van-primary-color);
    color: var(--van-white);
  }
}

.filter-panel__empty {
  font-size: 12px;
  color: var(--van-text-color-3);
}

.filter-panel__actions {
  display: flex;
  gap: 8px;
  padding-top: 4px;
  border-top: 1px solid var(--van-border-color);

  :deep(.van-button) {
    flex: 1;
  }
}
</style>
