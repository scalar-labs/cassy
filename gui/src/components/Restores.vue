<template>
    <div>
        <h4 class="pt-3">Restore Statuses in Cluster: {{cluster.cluster_name}}</h4>
        <ClusterSummary :cluster="cluster"/>
        <div class="row justify-content-end mb-3 pr-3">
            <button
                    type="button"
                    class="btn btn-primary mx-1"
                    @click="viewBackups()"
            >View Backups
            </button>
        </div>
        <table class="table table-bordered table-striped text-center">
            <thead class="table-success">
            <tr>
                <th scope="col">Snapshot ID</th>
                <th scope="col">Target IP</th>
                <th scope="col">Created At</th>
                <th scope="col">Updated At</th>
                <th scope="col">Restore Type</th>
                <th scope="col">Status</th>
            </tr>
            </thead>
            <tbody>
            <tr
                    v-for="(r, index) in displayedRestores"
                    :key="index"
            >
                <td>{{r.snapshot_id}}</td>
                <td>{{r.target_ip}}</td>
                <td>{{ parseInt(r.created_at) | moment('YYYY/M/D, h:mm a')}}</td>
                <td>{{ parseInt(r.updated_at) | moment('YYYY/M/D, h:mm a')}}</td>
                <td>{{r.restore_type}}</td>
                <td>{{r.status}}</td>
            </tr>
            </tbody>
        </table>
        <b-pagination
                class="justify-content-center"
                v-model="page"
                :per-page="perPage"
                :total-rows="restores.length"
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
      restores: Array,
      entryCount: Number,
      cluster: {},
    },
    data() {
      return {
        page: 1,
        perPage: 10,
      };
    },
    computed: {
      displayedRestores() {
        return this.paginate(this.restores);
      },
    },
    methods: {
      paginate(content) {
        let page = this.page;
        let perPage = this.perPage;
        let from = (page * perPage) - perPage;
        let to = (page * perPage);
        return content.slice(from, to);
      },
      viewBackups() {
        this.$router.push({name: 'ViewBackups', params: {cluster_id: this.cluster.cluster_id}});
      },
    },
  };
</script>
