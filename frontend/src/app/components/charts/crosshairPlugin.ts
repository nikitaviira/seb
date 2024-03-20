import {Chart, Plugin} from 'chart.js';
import {ChartEvent} from 'chart.js/dist/types';

const crosshairPlugin: Plugin = {
  id: 'crosshair',
  defaults: {
    width: 1,
    color: '#FF4949',
  },

  /* eslint-disable no-param-reassign */
  afterInit: (chart: Chart) => {
    chart.crosshair = {
      x: 0,
      y: 0,
    };
  },

  /* eslint-disable no-param-reassign */
  afterEvent: (chart: Chart, args: {event: ChartEvent; inChartArea: boolean}) => {
    const {inChartArea} = args;
    const {x, y} = args.event;
    chart.crosshair = {
      x,
      y,
      draw: inChartArea,
    };
    chart.draw();
  },

  beforeDatasetsDraw: (chart: Chart, _: any, opts: any) => {
    const {ctx} = chart;
    const {top, bottom} = chart.chartArea;
    const {x, draw} = chart.crosshair;
    if (!draw || !x) return;

    ctx.save();

    ctx.beginPath();
    ctx.lineWidth = opts.width;
    ctx.strokeStyle = opts.color;
    ctx.moveTo(x, bottom);
    ctx.lineTo(x, top);
    ctx.stroke();

    ctx.restore();
  },
};

export default crosshairPlugin;
