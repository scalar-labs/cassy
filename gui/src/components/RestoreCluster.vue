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
                            <select class="custom-select" id="restoreTypeSelect" v-model="restore_type">
                                <option v-for="(t, i) in availableRestoreTypes" :value="t" :key="i">{{ typeName(t) }}
                                </option>
                            </select>
                        </div>
                        <div class="form-group" v-if="restore_type === 2">
                            <label for="targetIpSelect">Specify Target IP (Optional)</label>
                            <select class="custom-select" id="targetIpSelect" v-model="target_ip">
                                <option value="">Choose a node ...</option>
                                <option v-for="(ip, index) in cluster.target_ips" :key="index">
                                    {{ip}}
                                </option>
                            </select>
                        </div>
                        <small v-if="failed" class="form-text text-danger">Failed to restore backup. Cassy service
                            unavailable.</small>
                        <div class="modal-footer">
                            <button class="btn btn-outline-secondary mx-1" data-toggle="modal"
                                    data-target="#restoreCluster">Cancel
                            </button>
                            <button class="btn btn-primary mx-1" @click="showConfirmationDialogue()">Restore Backup
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
  import $ from 'jquery';

  export default {
    props: {
      cluster: {},
      snapshot_id: String,
      backup_type: Number,
    },
    mounted() {
      $('#restoreCluster').on('hide.bs.modal', () => {
        this.target_ip = '';
        this.restore_type = 2;
      });
    },
    data() {
      return {
        failed: false,
        target_ip: '',
        restore_type: 2,
      };
    },
    computed: {
      availableRestoreTypes: function() {
        if (this.backup_type === 1) {
          return [2, 1];
        } else {
          return [2];
        }
      },
    },
    methods: {
      typeName(t) {
        return t === 2 ? 'Node' : 'Cluster';
      },
      showConfirmationDialogue() {
        let data = {
          restore_type: this.restore_type,
        };
        if (this.restore_type === 2) {
          data.snapshot_only = true;
          if (this.target_ip) {
            data.target_ips = [this.target_ip];
          }
        }
        if (this.snapshot_only) {
          data.snapshot_only = true;
        }

        $('#restoreCluster').modal('hide');
        $('#confirmRestore').modal('show');
        this.$emit('createRestoreRequestBody', data);
      },
    },
  };
</script>
