<template>
    <div>
        <h5>Backups in Cluster: {{cluster.cluster_name}}</h5>
        <div class="bg-secondary text-white border rounded my-3">
            <p class="m-2">CLUSTER_ID: {{cluster.cluster_id}}</p>
            <p class="m-2">TARGET_IPS: {{cluster.target_ips}}</p>
            <p class="m-2">KEYSPACES: {{cluster.keyspaces}}</p>
        </div>
        <table class="table text-nowrap text-center">
            <thead>
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
            <tbody v-for="(b, i) in backups_by_snapshot" :key="i" class="">
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
                            @click="viewRestoreHistory(e.snapshot_id)"
                    >View
                    </button>
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
        <div class="row justify-content-end mr-3">
            <button class="btn btn-outline-secondary mx-1" @click="goBack">Back</button>
            <button class="btn btn-primary mx-1" data-toggle="modal" data-target="#registerBackup">Create Backup
            </button>
        </div>
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
      goBack() {
        this.$router.back();
      },
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
      viewRestoreHistory(snapshot_id) {
        this.$router.push({ name: 'ViewRestores', params: { snapshot_id: snapshot_id} });
      }
    },
  };
</script>
<style>
    tbody::before {
        content: '';
        display: block;
        height: 30px;
    }
</style>