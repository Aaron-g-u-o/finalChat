<template>
  <div class="server-view">
    <div class="server-container">
      <!-- 服务器列表 -->
      <div class="server-list-section">
        <ServerList @serverSelected="handleServerSelected" />
      </div>
      
      <!-- 频道列表和语音频道 -->
      <div class="channel-section" v-if="selectedServer">
        <div class="channel-list-container">
          <ChannelList 
            :serverId="selectedServer.id" 
            @channelSelected="handleChannelSelected" 
          />
        </div>
        <div class="channel-content-container">
          <div v-if="selectedChannel" class="channel-content">
            <!-- 语音频道 -->
            <VoiceChannel 
              v-if="selectedChannel.type === 2" 
              :channelId="selectedChannel.id"
              :channelName="selectedChannel.name"
              :serverId="selectedServer.id"
            />
            <!-- 文字频道 -->
            <div v-else class="text-channel">
              <h3>{{ selectedChannel.name }}</h3>
              <p>文字频道功能开发中...</p>
            </div>
          </div>
          <div v-else class="empty-channel-content">
            <p>请选择一个频道</p>
          </div>
        </div>
      </div>
      <div v-else class="empty-server-section">
        <p>请选择一个服务器</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import ServerList from './components/ServerList/index.vue'
import ChannelList from './components/ChannelList/index.vue'
import VoiceChannel from './components/VoiceChannel/index.vue'
import type { ServerItem, ChannelItem } from '@/services/types'

const selectedServer = ref<ServerItem | null>(null)
const selectedChannel = ref<ChannelItem | null>(null)

// 处理服务器选择
const handleServerSelected = (server: ServerItem) => {
  selectedServer.value = server
  selectedChannel.value = null
}

// 处理频道选择
const handleChannelSelected = (channel: ChannelItem) => {
  selectedChannel.value = channel
}
</script>

<style lang="scss" scoped>
.server-view {
  height: 100%;
  background-color: #f5f7fa;

  .server-container {
    display: flex;
    height: 100%;

    .server-list-section {
      width: 300px;
      border-right: 1px solid #eaeaea;
      background-color: white;

      :deep(.server-list) {
        height: 100%;
      }
    }

    .channel-section {
      flex: 1;
      display: flex;

      .channel-list-container {
        width: 250px;
        border-right: 1px solid #eaeaea;
        background-color: white;

        :deep(.channel-list) {
          height: 100%;
        }
      }

      .channel-content-container {
        flex: 1;
        background-color: #f5f7fa;

        .channel-content {
          height: 100%;
        }

        .empty-channel-content {
          height: 100%;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #999;
        }

        .text-channel {
          height: 100%;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          color: #999;

          h3 {
            margin-bottom: 16px;
            color: #333;
          }
        }
      }
    }

    .empty-server-section {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #999;
    }
  }
}
</style>
