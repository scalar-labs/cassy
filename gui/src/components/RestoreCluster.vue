<template>
    <div class="modal fade" tabindex="-1" id="restoreCluster" aria-labelledby="restoreClusterModal" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Restore From Backup</h5>
                    <button class="close" type="button" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="form-group">
                            <label for="restoreTypeSelect">Select restore type</label>
                            <select class="custom-select" id="restoreTypeSelect" @change="setRestoreType">
                                <option selected value="2">Node</option>
                                <option value="1">Cluster</option>
                            </select>
                        </div>
                        <div class="form-group" v-if="restore_type === '2'">
                            <label for="targetIpSelect">Specify Target IP (Optional)</label>
                            <select class="custom-select" id="targetIpSelect">
                                <option selected>Choose a node ...</option>
                                <option v-for="(ip, index) in cluster.target_ips" :key="index" @change="setTargetIp">{{ip}}</option>
                            </select>
                        </div>
                        <div class="modal-footer">
                            <button class="btn btn-outline-secondary mx-1" data-toggle="modal"
                                    data-target="#restoreCluster">Cancel
                            </button>
                            <button class="btn btn-primary mx-1" data-toggle="modal" data-target="#restoreCluster"
                                    @click="restoreBackup()">Restore Backup
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
  export default {
    props: {
      cluster: {},
      target_ip: String,
      snapshot_id: String,
    },
    data() {
      return {
        restore_type: '2',
      };
    },
    methods: {
      setRestoreType() {
        let el = document.getElementById('restoreTypeSelect');
        if (el) {
          this.restore_type = el.value;
        }
      },
      setTargetIp() {
        let el = document.getElementById('targetIpSelect');
        if (el) {
          this.target_ip = el.value;
        }
      },
      restoreBackup() {
        let targetIps = this.target_ip;
        let data = {
          backup_type: this.backup_type,
        };
        if (this.backup_type === '2' && targetIps) {
          data.target_ips = targetIps;
        }
        this.$api.put(`clusters/${this.cluster.cluster_id}/data/${this.snapshot_id}`, data);
      },
    },
  };
</script>
