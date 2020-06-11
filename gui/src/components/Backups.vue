<template>
    <div>
        <h4 class="py-3">Backups in Cluster: {{cluster.cluster_name}}</h4>
        <table class="table table-bordered text-center">
            <thead class="table-success">
            <tr>
                <th scope="col">Cluster ID</th>
                <th scope="col">Target IPs</th>
                <th scope="col">Keyspaces</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td>{{cluster.cluster_id}}</td>
                <td>{{cluster.target_ips}}</td>
                <td>{{cluster.keyspaces}}</td>
            </tr>
            </tbody>
        </table>
        <div class="row justify-content-end mr-3 mb-3">
            <button class="btn btn-primary mx-1" data-toggle="modal" data-target="#registerBackup">Create Backup
            </button>
            <button
                    type="button"
                    class="btn btn-primary mx-1"
                    @click="viewRestoreHistory()"
            >View Restores
            </button>
        </div>
        <table class="table text-nowrap text-center table-bordered">
            <thead class="table-success">
            <tr>
                <th scope="col">Snapshot ID</th>
                <th scope="col">Backup Type</th>
                <th scope="col">Target IP</th>
                <th scope="col">Created At</th>
                <th scope="col">Updated At</th>
                <th scope="col">Status</th>
                <th scope="col"></th>
            </tr>
            </thead>
            <tbody id="mainTable" v-for="(b, i) in backups_by_snapshot" :key="i" class="">
            <tr v-for="(e, j) in b" :key="j">
                <td>{{e.snapshot_id}}</td>
                <td>{{backupType(e.backup_type)}}</td>
                <td>{{e.target_ip}}</td>
                <th class="font-weight-normal">{{ parseInt(e.created_at) | moment('YYYY/M/D, h:mm a') }}</th>
                <th class="font-weight-normal">{{ parseInt(e.updated_at) | moment('YYYY/M/D, h:mm a') }}</th>
                <td>{{e.status}}</td>
                <td>
                    <button
                            type="button"
                            class="btn btn-primary mx-1"
                            :disabled="isNotCompleted(e)"
                            data-toggle="modal"
                            data-target="#restoreCluster"
                            @click="$emit('emitRestoreParams', {snapshot_id: e.snapshot_id, backup_type: e.backup_type})"
                    >Restore
                    </button>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
  export default {
    props: {
      cluster: {
        type: Object,
      },
      backups: {
        type: Object,
      },
      backups_by_snapshot: Array,
    },
    methods: {
      backupType(type) {
        if (type === 1) {
          return 'Cluster-wide Snapshot';
        }
        if (type === 2) {
          return 'Snapshot';
        }
        if (type === 3) {
          return 'Incremental';
        }
      },
      isNotCompleted(entry) {
        if (entry.backup_type === 1) {
          for (let i = 0; i < this.backups_by_snapshot.length; i++) {
            let snapshot = this.backups_by_snapshot[i];
            for (let j = 0; j < snapshot.length; j++) {
              if (snapshot[j].snapshot_id === entry.snapshot_id) {
                if (snapshot[j].status !== 'COMPLETED') {
                  return true;
                }
              }
            }
          }
        } else {
          return entry.status !== 'COMPLETED';
        }
      },
      viewRestoreHistory() {
        this.$router.push({name: 'ViewRestores', params: {snapshot_id: ''}});
      },
    },
  };
</script>
<style>
    #mainTable::before {
        content: '';
        display: block;
        height: 30px;
    }
</style>
