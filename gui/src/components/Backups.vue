<template>
    <div>
        <h4 class="py-4">Backups</h4>
        <ClusterSummary :cluster="cluster"/>
        <div class="row justify-content-end mb-3 pr-3 pt-3">
            <button class="btn btn-primary mx-1" data-toggle="modal" data-target="#registerBackup">Create Backup
            </button>
            <button
                    type="button"
                    class="btn btn-primary mx-1"
                    @click="viewRestoreHistory()"
            >View Restores
            </button>
        </div>
        <table class="table text-left table-bordered">
            <thead class="table-secondary">
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
            <tbody :class="{'space-groups': i !== 0 }" v-for="(b, i) in displayedBackups" :key="i">
            <tr :class="{'no-data': backups_by_snapshot[0].length > 0, 'text-center': true, 'font-italic': true}">
                <td colspan="7">No backups were found for cluster {{cluster.cluster_name}}</td>
            </tr>
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
                            @click="$emit('emitRestoreParams', {snapshot_id: e.snapshot_id, backup_type: e.backup_type, target_ip: e.target_ip})"
                    >Restore
                    </button>
                </td>
            </tr>
            </tbody>
        </table>
        <b-pagination
                class="justify-content-center"
                v-model="page"
                :per-page="perPage"
                :total-rows="totalRows"
        >
        </b-pagination>
    </div>
</template>

<script>
  import ClusterSummary from './ClusterSummary';

  export default {
    components: {
      ClusterSummary,
    },
    props: {
      cluster: {
        type: Object,
      },
      backups_by_snapshot: Array,
    },
    data() {
      return {
        page: 1,
        perPage: 5,
      };
    },
    computed: {
      displayedBackups() {
        return this.paginate(this.backups_by_snapshot);
      },
      totalRows() {
        if (!this.backups_by_snapshot) { return 0; }
        return this.backups_by_snapshot.length;
      },
    },
    methods: {
      paginate(content) {
        if (!content) { return []; }
        let page = this.page;
        let perPage = this.perPage;
        let from = (page * perPage) - perPage;
        let to = (page * perPage);
        return content.slice(from, to);
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
        if (entry.backup_type !== 1) {
          return entry.status !== 'COMPLETED';
        }
        if (entry.backup_type === 1) {
          for (let i = 0; i < this.backups_by_snapshot.length; i++) {
            let snapshot = this.backups_by_snapshot[i];
            for (let j = 0; j < snapshot.length; j++) {
              if (snapshot[j].snapshot_id === entry.snapshot_id
                  && snapshot[j].backup_type === 1
                  && snapshot[j].status !== 'COMPLETED'
              ) {
                  return true;
              }
            }
          }
        }
      },
      viewRestoreHistory() {
        this.$router.push({name: 'ViewRestores'});
      },
    },
  };
</script>
<style>
    .space-groups::before {
        content: '';
        display: block;
        height: 30px;
    }
</style>
