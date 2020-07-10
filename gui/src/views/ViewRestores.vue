<template>
    <div class="col">
        <ErrorNotification :error_message="error_message"/>
        <Restores :restores="restores" :cluster="cluster"/>
    </div>
</template>

<script>
  import Restores from '../components/Restores';
  import ErrorNotification from '../components/ErrorNotification';

  export default {
    name: 'ViewRestores',
    props: {
      error_message: String,
    },
    components: {
      Restores,
      ErrorNotification,
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
        }).catch(error => {
          if (error.code === 503) {
            let message = "Failed to fetch restore information for cluster \"" + this.cluster_id + "\".";
            this.$emit('showError', message);
          }
        });
        this.$api.get(`clusters/${this.cluster_id}`).then((response) => {
          if (response.status === 200) {
            this.cluster = response.data.entries[0];
          }
        }).catch(error => {
          if (error.code === 503) {
            let message = "Failed to fetch cluster \"" + this.cluster_id + "\".";
            this.$emit('showError', message);
          }
        });
      },
    },
  };
</script>
