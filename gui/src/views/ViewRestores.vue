<template>
    <div class="col">
        <Restores :restores="restores" :cluster="cluster"/>
    </div>
</template>

<script>
  import Restores from '../components/Restores';

  export default {
    name: 'ViewRestores',
    components: {
      Restores,
    },
    data() {
      return {
        cluster_id: this.$route.params.cluster_id,
        restores: [],
        entryCount: 0,
        cluster: {},
        autoUpdateTimer: '',
      };
    },
    mounted() {
      this.fetchData();
      this.autoUpdateTimer = setInterval(this.fetchData, 10000);
    },
    beforeDestroy() {
      clearInterval(this.autoUpdateTimer);
    },
    methods: {
      fetchData() {
        this.$api.get(`clusters/${this.cluster_id}/data/`).then((response) => {
          if (response.status === 200) {
            this.restores = response.data.entries;
            this.entryCount = this.restores.length;
          }
        });
        this.$api.get(`clusters/${this.cluster_id}`).then((response) => {
          if (response.status === 200) {
            this.cluster = response.data.entries[0];
          }
        });
      },
    },
  };
</script>
