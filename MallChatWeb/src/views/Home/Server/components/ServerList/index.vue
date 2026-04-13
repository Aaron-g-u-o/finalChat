<template>
  <div class="server-list">
    <div class="server-list-header">
      <h3>服务器</h3>
      <button class="create-server-btn" @click="showCreateServerModal = true">
        <Icon name="plus" />
        创建服务器
      </button>
    </div>
    <div class="server-list-content">
      <div
        v-for="server in serverList"
        :key="server.id"
        class="server-item"
        @click="selectServer(server)"
      >
        <div class="server-icon">
          <img :src="server.icon" alt="server icon" />
        </div>
        <div class="server-info">
          <h4 class="server-name">{{ server.name }}</h4>
          <p class="server-description">{{ server.description }}</p>
          <div class="server-meta">
            <span class="member-count">{{ server.memberCount }} 成员</span>
          </div>
        </div>
        <div class="server-actions">
          <button class="action-btn" @click.stop="showUpdateServerModal(server)">
            <Icon name="edit" />
          </button>
          <button class="action-btn danger" @click.stop="deleteServer(server.id)">
            <Icon name="trash" />
          </button>
        </div>
      </div>
      <div v-if="serverList.length === 0" class="empty-server">
        <p>还没有服务器，创建一个新服务器吧</p>
      </div>
    </div>

    <!-- 创建服务器模态框 -->
    <el-dialog
      v-model="showCreateServerModal"
      title="创建服务器"
      width="500px"
    >
      <el-form :model="createServerForm" label-width="80px">
        <el-form-item label="服务器名称">
          <el-input v-model="createServerForm.name" placeholder="请输入服务器名称" />
        </el-form-item>
        <el-form-item label="服务器图标">
          <el-input v-model="createServerForm.icon" placeholder="请输入服务器图标URL" />
        </el-form-item>
        <el-form-item label="服务器描述">
          <el-input
            v-model="createServerForm.description"
            type="textarea"
            placeholder="请输入服务器描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCreateServerModal = false">取消</el-button>
          <el-button type="primary" @click="createServer">创建</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 更新服务器模态框 -->
    <el-dialog
      v-model="showUpdateServerModal"
      title="更新服务器"
      width="500px"
    >
      <el-form :model="updateServerForm" label-width="80px">
        <el-form-item label="服务器名称">
          <el-input v-model="updateServerForm.name" placeholder="请输入服务器名称" />
        </el-form-item>
        <el-form-item label="服务器图标">
          <el-input v-model="updateServerForm.icon" placeholder="请输入服务器图标URL" />
        </el-form-item>
        <el-form-item label="服务器描述">
          <el-input
            v-model="updateServerForm.description"
            type="textarea"
            placeholder="请输入服务器描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showUpdateServerModal = false">取消</el-button>
          <el-button type="primary" @click="updateServer">更新</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Icon from '@/components/Icon/index.vue'
import apis from '@/services/apis'
import type { ServerItem, CreateServerReq, UpdateServerReq } from '@/services/types'

const emit = defineEmits<{
  (e: 'serverSelected', server: ServerItem): void
}>()

const router = useRouter()
const serverList = ref<ServerItem[]>([])
const showCreateServerModal = ref(false)
const showUpdateServerModal = ref(false)

const createServerForm = ref<CreateServerReq>({
  name: '',
  icon: '',
  description: '',
})

const updateServerForm = ref<UpdateServerReq>({
  id: 0,
  name: '',
  icon: '',
  description: '',
})

// 获取服务器列表
const getServerList = async () => {
  try {
    const res = await apis.getServerList()
    serverList.value = res.data
  } catch (error) {
    console.error('获取服务器列表失败', error)
  }
}

// 创建服务器
const createServer = async () => {
  try {
    await apis.createServer(createServerForm.value)
    showCreateServerModal.value = false
    getServerList()
    // 重置表单
    createServerForm.value = {
      name: '',
      icon: '',
      description: '',
    }
  } catch (error) {
    console.error('创建服务器失败', error)
  }
}

// 显示更新服务器模态框
const showUpdateServerModal = (server: ServerItem) => {
  updateServerForm.value = {
    id: server.id,
    name: server.name,
    icon: server.icon,
    description: server.description,
  }
  showUpdateServerModal.value = true
}

// 更新服务器
const updateServer = async () => {
  try {
    await apis.updateServer(updateServerForm.value)
    showUpdateServerModal.value = false
    getServerList()
  } catch (error) {
    console.error('更新服务器失败', error)
  }
}

// 删除服务器
const deleteServer = async (serverId: number) => {
  try {
    await apis.deleteServer({ id: serverId })
    getServerList()
  } catch (error) {
    console.error('删除服务器失败', error)
  }
}

// 选择服务器
const selectServer = (server: ServerItem) => {
  // 触发服务器选择事件
  emit('serverSelected', server)
}

// 组件挂载时获取服务器列表
onMounted(() => {
  getServerList()
})
</script>

<style lang="scss" scoped>
.server-list {
  height: 100%;
  display: flex;
  flex-direction: column;

  .server-list-header {
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

    .create-server-btn {
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

  .server-list-content {
    flex: 1;
    overflow-y: auto;
    padding: 16px;

    .server-item {
      display: flex;
      align-items: center;
      padding: 16px;
      border: 1px solid #eaeaea;
      border-radius: 8px;
      margin-bottom: 12px;
      cursor: pointer;

      &:hover {
        border-color: #409eff;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }

      .server-icon {
        width: 64px;
        height: 64px;
        border-radius: 8px;
        overflow: hidden;
        margin-right: 16px;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .server-info {
        flex: 1;

        .server-name {
          margin: 0 0 8px 0;
          font-size: 16px;
          font-weight: 600;
        }

        .server-description {
          margin: 0 0 8px 0;
          font-size: 14px;
          color: #666;
          line-height: 1.4;
        }

        .server-meta {
          font-size: 12px;
          color: #999;

          .member-count {
            margin-right: 16px;
          }
        }
      }

      .server-actions {
        display: flex;
        gap: 8px;

        .action-btn {
          width: 32px;
          height: 32px;
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
        }
      }
    }

    .empty-server {
      text-align: center;
      padding: 60px 0;
      color: #999;
    }
  }
}
</style>
