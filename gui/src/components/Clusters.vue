<template>
    <div>
        <h4 class="py-4">Clusters</h4>
        <div class="row justify-content-end mb-3 pr-3 pt-3">
            <button class="btn btn-primary mx-1" type="button" data-toggle="modal" data-target="#registerCluster">Add Cluster</button>
        </div>
        <table class="table table-bordered text-left">
            <thead class="table-secondary">
            <tr>
                <th scope="col">ID</th>
                <th scope="col">Name</th>
                <th scope="col">Created At</th>
                <th scope="col">Updated At</th>
                <th scope="col"></th>
            </tr>
            </thead>
            <tbody>
            <tr :class="{'no-data': displayedClusters.length > 0, 'text-center': true, 'font-italic': true}">
                <td colspan="5">No clusters were found</td>
            </tr>
            <tr
                    v-for="(c, index) in displayedClusters"
                    :key="index"
            >
                <td>{{c.cluster_id}}</td>
                <td>{{c.cluster_name}}</td>
                <td>{{ parseInt(c.created_at) | moment('YYYY/M/D, h:mm a')}}</td>
                <td>{{ parseInt(c.updated_at) | moment('YYYY/M/D h:mm a')}}</td>
                <td>
                    <button
                            type="button"
                            class="btn btn-primary"
                            @click="viewBackups(c.cluster_id)"
                    >Manage
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
  export default {
    props: {
      entries: Array,
      entryCount: Number,
    },
    data() {
      return {
        page: 1,
        perPage: 10,
      }
    },
    computed: {
      displayedClusters() {
        return this.paginate(this.entries);
      },
      totalRows() {
        if (!this.entries) { return 0; }
        return this.entries.length;
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
      viewBackups(cluster_id) {
        this.$router.push(`/clusters/${cluster_id}/backups`);
      },
    },
  };
</script>
