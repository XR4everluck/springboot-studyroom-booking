<template>
  <el-card>
    <!--    配套信息管理-->
    <SupportFacility></SupportFacility>
    <!--    标签信息管理-->
    <SupportLabel class="m-t-20"></SupportLabel>
    <!--    座位基本信息管理-->
    <SupportSeatBase class="m-t-20"></SupportSeatBase>
    <!--    杂费信息管理-->
    <SupportFee class="m-t-20"></SupportFee>
    <!--    租期管理-->
    <SupportTerm class="m-t-20"></SupportTerm>
    <!--    支付方式管理-->
    <SupportPayment class="m-t-20"></SupportPayment>
  </el-card>
</template>

<script setup lang="tsx">
import SupportFacility from '@/views/storeManagement/attributeManagement/components/supportFacility.vue'
import SupportLabel from '@/views/storeManagement/attributeManagement/components/supportLabel.vue'
import SupportSeatBase from '@/views/storeManagement/attributeManagement/components/supportSeatBase.vue'
import SupportFee from '@/views/storeManagement/attributeManagement/components/supportFee.vue'
import SupportTerm from '@/views/storeManagement/attributeManagement/components/supportTerm.vue'
import SupportPayment from '@/views/storeManagement/attributeManagement/components/supportPayment.vue'
</script>
