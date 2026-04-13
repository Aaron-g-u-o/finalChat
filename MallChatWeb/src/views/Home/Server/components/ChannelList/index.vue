<template>
  <div class="channel-list">
    <div class="channel-list-header">
      <h3>频道</h3>
      <button class="create-channel-btn" @click="showCreateChannelModal = true">
        <Icon name="plus" />
        创建频道
      </button>
    </div>
    <div class="channel-list-content">
      <div
        v-for="channel in channelList"
        :key="channel.id"
        class="channel-item"
        :class="{ 'voice-channel': channel.type === 2 }"
        @click="selectChannel(channel)"
      >
        <div class="channel-icon">
          <Icon v-if="channel.type === 1" name="message" />
          <Icon v-else name="voice" />
        </div>
        <div class="channel-info">
          <h4 class="channel-name">{{ channel.name }}</h4>
          <div class="channel-meta">
            <span class="channel-type">{{ channel.type === 1 ? '文字频道' : '语音频道' }}</span>
          </div>
        </div>
        <div class="channel-actions">
          <button class="action-btn" @click.stop="showUpdateChannelModal(channel)">
            <Icon name="edit" />
          </button>
          <button class="action-btn danger" @click.stop="deleteChannel(channel.id)">
            <Icon name="trash" />
          </button>
        </div>
      </div>
      <div v-if="channelList.length === 0" class="empty-channel">
        <p>还没有频道，创建一个新频道吧</p>
      </div>
    </div>

    <!-- 创建频道模态框 -->
    <el-dialog
      v-model="showCreateChannelModal"
      title="创建频道"
      width="500px"
    >
      <el-form :model="createChannelForm" label-width="80px">
        <el-form-item label="频道名称">
          <el-input v-model="createChannelForm.name" placeholder="请输入频道名称" />
        </el-form-item>
        <el-form-item label="频道类型">
          <el-radio-group v-model="createChannelForm.type">
            <el-radio label="1">文字频道</el-radio>
            <el-radio label="2">语音频道</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="createChannelForm.sort" :min="1" :max="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCreateChannelModal = false">取消</el-button>
          <el-button type="primary" @click="createChannel">创建</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 更新频道模态框 -->
    <el-dialog
      v-model="showUpdateChannelModal"
      title="更新频道"
      width="500px"
    >
      <el-form :model="updateChannelForm" label-width="80px">
        <el-form-item label="频道名称">
          <el-input v-model="updateChannelForm.name" placeholder="请输入频道名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="updateChannelForm.sort" :min="1" :max="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showUpdateChannelModal = false">取消</el-button>
          <el-button type="primary" @click="updateChannel">更新</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import Icon from '@/components/Icon/index.vue'
import apis from '@/services/apis'
import type { ChannelItem, CreateChannelReq, UpdateChannelReq } from '@/services/types'

const props = defineProps<{
  serverId: number
}>()

const emit = defineEmits<{
  (e: 'channelSelected', channel: ChannelItem): void
}>()

const channelList = ref<ChannelItem[]>([])
const showCreateChannelModal = ref(false)
const showUpdateChannelModal = ref(false)

const createChannelForm = ref<CreateChannelReq>({
  serverId: props.serverId,
  name: '',
  type: 1,
  sort: 1,
})

const updateChannelForm = ref<UpdateChannelReq>({
  id: 0,
  name: '',
  sort: 1,
})

// 监听serverId变化，重新获取频道列表
watch(
  () => props.serverId,
  (newServerId) => {
    if (newServerId) {
      createChannelForm.value.serverId = newServerId
      getChannelList()
    }
  },
  { immediate: true }
)

// 获取频道列表
const getChannelList = async () => {
  if (!props.serverId) return
  
  try {
    const res = await apis.getChannelList({ serverId: props.serverId })
    channelList.value = res.data
  } catch (error) {
    console.error('获取频道列表失败', error)
  }
}

// 创建频道
const createChannel = async () => {
  try {
    await apis.createChannel(createChannelForm.value)
    showCreateChannelModal.value = false
    getChannelList()
    // 重置表单
    createChannelForm.value = {
      serverId: props.serverId,
      name: '',
      type: 1,
      sort: 1,
    }
  } catch (error) {
    console.error('创建频道失败', error)
  }
}

// 显示更新频道模态框
const showUpdateChannelModal = (channel: ChannelItem) => {
  updateChannelForm.value = {
    id: channel.id,
    name: channel.name,
    sort: channel.sort,
  }
  showUpdateChannelModal.value = true
}

// 更新频道
const updateChannel = async () => {
  try {
    await apis.updateChannel(updateChannelForm.value)
    showUpdateChannelModal.value = false
    getChannelList()
  } catch (error) {
    console.error('更新频道失败', error)
  }
}

// 删除频道
const deleteChannel = async (channelId: number) => {
  try {
    await apis.deleteChannel({ id: channelId })
    getChannelList()
  } catch (error) {
    console.error('删除频道失败', error)
  }
}

// 选择频道
const selectChannel = (channel: ChannelItem) => {
  emit('channelSelected', channel)
}

// 组件挂载时获取频道列表
onMounted(() => {
  getChannelList()
})
</script>

<style lang="scss" scoped>
.channel-list {
  height: 100%;
  display: flex;
  flex-direction: column;

  .channel-list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    border-bottom: 1px solid #eaeaea;

    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
    }

    .create-channel-btn {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 8px 16px;
      background-color: #409eff;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;

      &:hover {
        background-color: #66b1ff;
      }
    }
  }

  .channel-list-content {
    flex: 1;
    overflow-y: auto;
    padding: 16px;

    .channel-item {
      display: flex;
      align-items: center;
      padding: 12px 16px;
      border: 1px solid #eaeaea;
      border-radius: 8px;
      margin-bottom: 8px;
      cursor: pointer;

      &:hover {
        border-color: #409eff;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }

      &.voice-channel {
        border-left: 4px solid #67c23a;
      }

      .channel-icon {
        width: 32px;
        height: 32px;
        border-radius: 4px;
        background-color: #f5f7fa;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;

        :deep(.icon) {
          font-size: 16px;
        }
      }

      .channel-info {
        flex: 1;

        .channel-name {
          margin: 0 0 4px 0;
          font-size: 14px;
          font-weight: 500;
        }

        .channel-meta {
          font-size: 12px;
          color: #999;

          .channel-type {
            margin-right: 12px;
          }
        }
      }

      .channel-actions {
        display: flex;
        gap: 8px;

        .action-btn {
          width: 28px;
          height: 28px;
          border: 1px solid #eaeaea;
          border-radius: 4px;
          background-color: white;
          cursor: pointer;
          display: flex;
          align-items: center;
          justify-content: center;

          &:hover {
            border-color: #409eff;
            color: #409eff;
          }

          &.danger:hover {
            border-color: #f56c6c;
            color: #f56c6c;
          }

          :deep(.icon) {
            font-size: 14px;
          }
        }
      }
    }

    .empty-channel {
      text-align: center;
      padding: 40px 0;
      color: #999;
    }
  }
}
</style>
