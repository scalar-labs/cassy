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
                            <label for="targetIpsTextArea">Specify nodes (Optional)</label>
                            <textarea class="form-control" id="targetIpsTextArea" rows="3"
                                      placeholder="192.168.0.1, 192.168.0.2, 192.168.0.3">
                            </textarea>
                            <small class="form-text text-muted">Enter the node IP addresses separated by a
                                comma.</small>
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
      parseTargetIps() {
        let el = document.getElementById('targetIpsTextArea');
        if (el) {
          let targetIps = el.value;
          if (targetIps) {
            return targetIps.replace(/\s+/g, '').split(',');
          }
        }
      },
      restoreBackup() {
        this.$api.put(`clusters/${this.cluster.cluster_id}/data/${this.snapshot_id}`, {
          restore_type: this.restore_type,
          target_ips: this.parseTargetIps(),
        });
      },
    },
  };
</script>
