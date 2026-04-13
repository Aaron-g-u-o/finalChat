<template>
  <div class="voice-channel">
    <div class="voice-channel-header">
      <h3>{{ channelName }}</h3>
      <div class="voice-channel-actions">
        <button
          class="join-leave-btn"
          :class="{ 'leave-btn': isJoined }"
          @click="isJoined ? leaveChannel() : joinChannel()"
        >
          <Icon v-if="!isJoined" name="voice" />
          <Icon v-else name="close" />
          {{ isJoined ? '离开频道' : '加入频道' }}
        </button>
      </div>
    </div>
    <div class="voice-channel-content">
      <div class="voice-channel-info">
        <div class="info-item">
          <span class="info-label">频道类型:</span>
          <span class="info-value">语音频道</span>
        </div>
        <div class="info-item">
          <span class="info-label">在线人数:</span>
          <span class="info-value">{{ onlineCount }}</span>
        </div>
      </div>
      <div class="voice-channel-members">
        <h4>成员列表</h4>
        <div class="members-list">
          <div
            v-for="member in membersList"
            :key="member.userId"
            class="member-item"
          >
            <div class="member-avatar">
              <img :src="member.avatar" alt="member avatar" />
              <div class="speaking-indicator" v-if="member.isSpeaking"></div>
            </div>
            <div class="member-info">
              <h5 class="member-name">{{ member.userName }}</h5>
              <span class="member-status">{{ member.isSpeaking ? '正在说话' : '在线' }}</span>
            </div>
          </div>
          <div v-if="membersList.length === 0" class="empty-members">
            <p>还没有成员加入该语音频道</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import Icon from '@/components/Icon/index.vue'
import apis from '@/services/apis'
import type { VoiceChannelInfo, VoiceMemberItem, JoinVoiceChannelReq, LeaveVoiceChannelReq } from '@/services/types'

const props = defineProps<{
  channelId: number
  channelName: string
  serverId: number
}>()

const isJoined = ref(false)
const onlineCount = ref(0)
const membersList = ref<VoiceMemberItem[]>([])

// 监听channelId变化，重新获取频道信息
watch(
  () => props.channelId,
  (newChannelId) => {
    if (newChannelId) {
      getChannelInfo()
      getChannelMembers()
    }
  },
  { immediate: true }
)

// 获取语音频道信息
const getChannelInfo = async () => {
  if (!props.channelId) return
  
  try {
    const res = await apis.getVoiceChannelInfo({ channelId: props.channelId })
    onlineCount.value = res.data.onlineCount
  } catch (error) {
    console.error('获取语音频道信息失败', error)
  }
}

// 获取语音频道成员列表
const getChannelMembers = async () => {
  if (!props.channelId) return
  
  try {
    const res = await apis.getVoiceChannelMembers({ channelId: props.channelId })
    membersList.value = res.data
  } catch (error) {
    console.error('获取语音频道成员列表失败', error)
  }
}

// 加入语音频道
const joinChannel = async () => {
  try {
    const req: JoinVoiceChannelReq = {
      serverId: props.serverId,
      channelId: props.channelId,
    }
    await apis.joinVoiceChannel(req)
    isJoined.value = true
    getChannelInfo()
    getChannelMembers()
    // 这里可以添加WebSocket连接逻辑，用于处理语音信令
    console.log('加入语音频道成功')
  } catch (error) {
    console.error('加入语音频道失败', error)
  }
}

// 离开语音频道
const leaveChannel = async () => {
  try {
    const req: LeaveVoiceChannelReq = {
      channelId: props.channelId,
    }
    await apis.leaveVoiceChannel(req)
    isJoined.value = false
    getChannelInfo()
    getChannelMembers()
    // 这里可以添加关闭WebSocket连接的逻辑
    console.log('离开语音频道成功')
  } catch (error) {
    console.error('离开语音频道失败', error)
  }
}

// 组件挂载时获取频道信息和成员列表
onMounted(() => {
  getChannelInfo()
  getChannelMembers()
})
</script>

<style lang="scss" scoped>
.voice-channel {
  height: 100%;
  display: flex;
  flex-direction: column;

  .voice-channel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    border-bottom: 1px solid #eaeaea;
    background-color: #f8f9fa;

    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
    }

    .voice-channel-actions {
      .join-leave-btn {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 8px 16px;
        background-color: #67c23a;
        color: white;
        border: none;
        border-radius: 4px;
        cursor: pointer;

        &:hover {
          background-color: #85ce61;
        }

        &.leave-btn {
          background-color: #f56c6c;

          &:hover {
            background-color: #f78989;
          }
        }
      }
    }
  }

  .voice-channel-content {
    flex: 1;
    overflow-y: auto;
    padding: 16px;

    .voice-channel-info {
      background-color: white;
      border-radius: 8px;
      padding: 16px;
      margin-bottom: 20px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);

      .info-item {
        display: flex;
        margin-bottom: 8px;

        .info-label {
          width: 80px;
          font-size: 14px;
          color: #666;
        }

        .info-value {
          font-size: 14px;
          font-weight: 500;
        }
      }
    }

    .voice-channel-members {
      background-color: white;
      border-radius: 8px;
      padding: 16px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);

      h4 {
        margin: 0 0 16px 0;
        font-size: 16px;
        font-weight: 600;
      }

      .members-list {
        .member-item {
          display: flex;
          align-items: center;
          padding: 12px;
          border-bottom: 1px solid #f0f0f0;

          &:last-child {
            border-bottom: none;
          }

          .member-avatar {
            position: relative;
            width: 48px;
            height: 48px;
            border-radius: 50%;
            overflow: hidden;
            margin-right: 12px;

            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }

            .speaking-indicator {
              position: absolute;
              bottom: 0;
              right: 0;
              width: 12px;
              height: 12px;
              border-radius: 50%;
              background-color: #f56c6c;
              border: 2px solid white;
              animation: pulse 1.5s infinite;
            }
          }

          .member-info {
            flex: 1;

            .member-name {
              margin: 0 0 4px 0;
              font-size: 14px;
              font-weight: 500;
            }

            .member-status {
              font-size: 12px;
              color: #999;
            }
          }
        }

        .empty-members {
          text-align: center;
          padding: 40px 0;
          color: #999;
        }
      }
    }
  }
}

@keyframes pulse {
  0% {
    transform: scale(0.8);
    opacity: 1;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.7;
  }
  100% {
    transform: scale(0.8);
    opacity: 1;
  }
}
</style>
